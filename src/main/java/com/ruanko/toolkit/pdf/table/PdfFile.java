package com.ruanko.toolkit.pdf.table;

import java.io.File;

import org.ghost4j.document.PDFDocument;

import com.ruanko.toolkit.pdf.PDF2PNG;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class PdfFile {
	final static String[] unit = { "Byte", "KiB", "MiB", "GiB", "TiB" };

	public enum State {
		Ready, Waiting, Process, Done;
	}
	
	private CheckBox select;
	private StringProperty name;
	private StringProperty path;
	private IntegerProperty pageCount;
	private StringProperty fileSize;
	private BooleanProperty busy;
	private Button open;
	private Button export;

	private File file = null;
	private PDFDocument doc = null;

	public PdfFile(File pdfFile) throws Exception {
		this.file = pdfFile;
		this.doc = new PDFDocument();
		this.doc.load(pdfFile);

		select = new CheckBox();
		name = new SimpleStringProperty(pdfFile.getName());
		path = new SimpleStringProperty(pdfFile.getAbsolutePath());
		pageCount = new SimpleIntegerProperty(doc.getPageCount());
		busy = new SimpleBooleanProperty(false);

		float size = doc.getSize();
		int u = 0;
		while (u < unit.length && size > 1024) {
			u++;
			size /= 1024f;
		}
		fileSize = new SimpleStringProperty(String.format("%.2f %s", size, unit[u]));

		open = new Button("打开");
		export = new Button("转换png");
		export.setOnAction(e -> {
			Platform.runLater(new Runnable() {
				public void run() {
					setBusy(true);
					export.setDisable(true);
					PDF2PNG.convertPdfToPng(file.getAbsolutePath());
					export.setDisable(false);
					setBusy(false);
				}
			});
		});
	}

	public File getFile() {
		return file;
	}

	public PDFDocument getDoc() {
		return doc;
	}

	public void setSelect(boolean select) {
		this.select.setSelected(select);
	}

	public CheckBox getSelect() {
		return select;
	}

	public String getName() {
		return name.get();
	}

	public String getPath() {
		return path.get();
	}

	public int getPageCount() {
		return pageCount.get();
	}

	public String getFileSize() {
		return fileSize.get();
	}

	public void setBusy(boolean busy) {
		this.busy.set(busy);
	}

	public boolean isBusy() {
		return busy.get();
	}
	
	public String getBusy() {
		return busy.get() ? "忙碌" : "就绪";
	}

	public Button getOpen() {
		return open;
	}

	public Button getExport() {
		return export;
	}
}
