package com.ruanko.toolkit.pdf;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

import static com.ruanko.toolkit.pdf.State.Process;
import static com.ruanko.toolkit.pdf.State.Done;
import static com.ruanko.toolkit.pdf.State.Ready;

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
	public final static String IMAGE_NAME = "%s" + File.separatorChar + "%d.png";
	
	private PdfFile pdf;

	protected ProcessTask(PdfFile pdfFile) {
		this.pdf = pdfFile;
	}
	
	@Override
	protected void succeeded() {
		super.succeeded();
	}

	@Override
	protected Void call() throws Exception {
		try {
			pdf.setState(Process);
			updateMessage("开始");
			
			/**
			 * 生成书签
			 */
			PdfReader reader = new PdfReader(pdf.getFile().getAbsolutePath());
			List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);

			if (list == null) {
				logger.info("无书签");
			} else {
				for (Iterator<HashMap<String, Object>> i = list.iterator(); i.hasNext();) {
					showBookmark(i.next(), 0);
				}
			}
			
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
				
				
				if (isCancelled()) {
					break;
				}
				updateProgress(page+1, pages);
				updateMessage((page+1) + "/" + pages);
				
				
				List<Image> images = renderer.render(document, page, page);

				for (int i = 0; i < images.size(); i++) {
					saveImage((RenderedImage) images.get(i), dirname, page + i + 1);
				}
				
			}

			logger.debug("End PDF conversion at " + new Date());
			
			updateMessage("完成");
			pdf.setState(Done);
			
		} catch (Exception e) {
			logger.error("ERROR: {}", e.getMessage(), e);
		}
		
		updateMessage("就绪");
		pdf.setState(Ready);
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
			return parent + File.separator + dirname;
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

	String[] spaces = {"", "  ", "    ", "      ", "        "};
	/**
	 * 获取标题
	 * @param bookmark
	 * @param depth
	 */
	private void showBookmark(HashMap<String, Object> bookmark, int depth) {
		// 获取标题
		if ("GoTo".equals(bookmark.get("Action"))) {
			String title = (String) bookmark.get("Title");

			// 获取页码
			int pageNum = -1;
			String page = (String) bookmark.get("Page");
			if (page != null) {

				page = page.trim();

				int idx = page.indexOf(' ');


				if (idx < 0) {
					pageNum = Integer.parseInt(page);
				} else {
					pageNum = Integer.parseInt(page.substring(0, idx));
				}
			}
			
			logger.info("{}{} ------- {}", spaces[depth], title, pageNum);
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> kids = (ArrayList<HashMap<String, Object>>) bookmark.get("Kids");
		if (kids == null)
			return;
		for (Iterator<HashMap<String, Object>> i = kids.iterator(); i.hasNext();) {

			showBookmark(i.next(), depth+1);
		}
	}
}
