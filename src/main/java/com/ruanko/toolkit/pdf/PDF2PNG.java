package com.ruanko.toolkit.pdf;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDF2PNG {

	private static Logger logger = LoggerFactory.getLogger(PDF2PNG.class);
	
	public final static String OUTPUT = "output";
	
	public final static String DATE_FORMAT = "yyyyMMddHHmmss";
	public final static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	
	// FORMAT: output/{fileName}_{yyyyMMddHHmmss}/001.png
	public final static String IMAGE_NAME = "%s" + File.separatorChar + "%s" + File.separatorChar + "%03d.png";

	public static void main(String[] args) {
		convertPdfToPng("Learn JavaFX 8(Apress,2015).pdf");
	}

	public static void convertPdfToPng(String pdfFileName) {

		try {
			logger.debug("Begin converting PDF: {} at {}", pdfFileName, format.format(new Date()));
			
			PDFDocument document = new PDFDocument();
			document.load(new File(pdfFileName));

			int pages = document.getPageCount();
			logger.debug("Total pages : {}", pages);
			
			// create renderer
			SimpleRenderer renderer = new SimpleRenderer();
			// set resolution (in DPI)
			renderer.setResolution(150);
			
			// Create the directory
			String dirname = mkOutputDir(pdfFileName);
			
			// render
			for(int page = 0; page<pages; page++) {
				List<Image> images = renderer.render(document, page, page);
				
				for (int i = 0; i < images.size(); i++) {
					saveImage((RenderedImage) images.get(i), dirname, page+i+1);
				}
			}

			logger.debug("End PDF conversion at " + new Date());


		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage(), e);
		}
	}
	
	public static String getSimpleName(String pdfFileName) {
		String filename = FilenameUtils.removeExtension(pdfFileName);
		int idx = FilenameUtils.indexOfLastSeparator(filename);
		if (idx != -1) {
			filename = filename.substring(idx+1);
		}
		
		return filename;
	}
	
	public static String mkOutputDir(String pdfFileName) {
		String filename = getSimpleName(pdfFileName);
		
		// Create the directory
		String dirname = filename + "_" + format.format(new Date());
		File dir = new File(OUTPUT + File.separatorChar + dirname);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		logger.debug("New directory created: {}", dirname);
		
		return dirname;
	}
	/**
	 * write images to files to disk as PNG
	 * @param renderedImage
	 * @param dirname
	 * @param page
	 */
	public static void saveImage(RenderedImage renderedImage, String dirname, int page) {
		try {
			String formatName = "png";
	
			String outputFilePath = String.format(IMAGE_NAME, OUTPUT, dirname, page);
	
			File outputFile = new File(outputFilePath);
			ImageIO.write(renderedImage, formatName, outputFile);
		} catch (IOException e) {
			logger.error("IOERROR: {}", e.getMessage(), e);
		}
	}
}
