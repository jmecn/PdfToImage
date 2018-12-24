package net.jmecn.toolkit.pdf;

import java.io.File;

import org.ghost4j.document.PDFDocument;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * PdfFile
 * 
 * @author yanmaoyuan
 *
 */
public class PdfFile {
	final static String[] unit = { "Byte", "KiB", "MiB", "GiB", "TiB" };

	private State state = State.Ready;
	
	private SimpleBooleanProperty select;
	private String name;
	private String path;
	private int pageCount;
	private String fileSize;
	private StringProperty status;
	private DoubleProperty progress;

	private File file = null;
	private PDFDocument doc = null;

	public PdfFile(File pdfFile) throws Exception {
		this.file = pdfFile;
		this.doc = new PDFDocument();
		this.doc.load(pdfFile);

		select = new SimpleBooleanProperty(false);
		name = pdfFile.getName();
		path = pdfFile.getAbsolutePath();
		pageCount = doc.getPageCount();
		status = new SimpleStringProperty("就绪");

		float size = doc.getSize();
		int u = 0;
		while (u < unit.length && size > 1024) {
			u++;
			size /= 1024f;
		}
		fileSize = String.format("%.2f %s", size, unit[u]);

		progress = new SimpleDoubleProperty(0);
	}

	public File getFile() {
		return file;
	}

	public PDFDocument getDoc() {
		return doc;
	}

	public void setSelect(boolean select) {
		this.select.set(select);
	}

	public boolean getSelect() {
		return select.get();
	}
	
	public SimpleBooleanProperty selectProperty() {
		return select;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public int getPageCount() {
		return pageCount;
	}

	public String getFileSize() {
		return fileSize;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getStatus() {
		return status.get();
	}
	
	public StringProperty statusProperty() {
		return status;
	}

	public double getProgress() {
		return progress.get();
	}
	
	public DoubleProperty progressProperty() {
		return progress;
	}

}
