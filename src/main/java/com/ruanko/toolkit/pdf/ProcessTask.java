package com.ruanko.toolkit.pdf;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruanko.toolkit.pdf.Settings.OutputModel;

import javafx.concurrent.Task;

/**
 * PDF文件处理任务
 * 
 * @author yanmaoyuan
 *
 */
public class ProcessTask extends Task<Void> {
	static Logger logger = LoggerFactory.getLogger(ProcessTask.class);

	public final static String DATE_FORMAT = "yyyyMMddHHmmss";
	public final static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	// FORMAT: output/{fileName}_{yyyyMMddHHmmss}/001.png
	public final static String IMAGE_NAME = "%s" + File.separatorChar + "%04d.png";
	
	private PdfFile pdf;

	protected ProcessTask(PdfFile pdfFile) {
		this.pdf = pdfFile;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			updateMessage("Process..");
			
			logger.debug("Begin converting PDF: {} at {}", pdf.getName(), format.format(new Date()));

			PDFDocument document = pdf.getDoc();

			int pages = document.getPageCount();
			logger.debug("Total pages : {}", pages);

			// create renderer
			SimpleRenderer renderer = new SimpleRenderer();
			// set resolution (in DPI)
			renderer.setResolution(Settings.get().getDpi());

			// Create the directory
			String dirname = getOutput();
			File folder = new File(dirname);
			if (!folder.exists()) {
				folder.mkdirs();
				logger.info("Create new directory: {}", folder);
			}

			// render
			for (int page = 0; page < pages; page++) {
				
				updateProgress(page+1, pages);
				updateMessage((page+1) + "/" + pages);
				
				List<Image> images = renderer.render(document, page, page);

				for (int i = 0; i < images.size(); i++) {
					saveImage((RenderedImage) images.get(i), dirname, page + i + 1);
				}
				
			}

			logger.debug("End PDF conversion at " + new Date());
			
			updateMessage("Done.");

		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 计算输出文件的路径
	 * @param model
	 * @return
	 */
	private String getOutput() {
		
		// 计算文件名
		String fileName = pdf.getName();
		int idx = fileName.lastIndexOf(".");
		fileName = fileName.substring(0, idx);
		String dirname = fileName + "_" + format.format(new Date());
		
		OutputModel model = Settings.get().getOutputModel();
		switch (model) {
		case Relative: {
			String parent = pdf.getFile().getParent();
			return parent + File.separator + "output" + File.separator + dirname;
		}
		case Absolute: {
			return Settings.get().getOutput();
		}
		case Local:
		default:
			return "./output" + File.separator + dirname;
		}
	}

	/**
	 * write images to files to disk as PNG
	 * 
	 * @param renderedImage
	 * @param dirname
	 * @param page
	 */
	private void saveImage(RenderedImage renderedImage, String dirname, int page) {
		try {
			String formatName = "png";

			String outputFilePath = String.format(IMAGE_NAME, dirname, page);

			File outputFile = new File(outputFilePath);
			ImageIO.write(renderedImage, formatName, outputFile);
		} catch (IOException e) {
			logger.error("IOERROR: {}", e.getMessage(), e);
		}
	}

}
