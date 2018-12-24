package net.jmecn.toolkit.pdf;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PdfList extends LinkedList<PdfFile> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(PdfList.class);

	private final ObservableList<PdfFile> data = FXCollections.observableArrayList();
	
	public ObservableList<PdfFile> getData() {
		return data;
	}
	
	/**
	 * 检查是否已经添加过此文件。
	 * @param file
	 * @return
	 */
	public boolean contains(File file) {
		int row = size();
		for(int i=0; i<row; i++) {
			if (get(i).getFile().equals(file)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 判断一个文件是否是pdf文件
	 * @param file
	 * @return
	 */
	public boolean isPdf(File file) {
		String name = file.getName().toLowerCase();
		name.endsWith(".pdf");
		return (file.exists() && file.isFile() && name.endsWith(".pdf"));
	}
	
	/**
	 * 用户添加一系列文件到队列中时，对其进行过滤。过滤规则如下：
	 * 1. 文件
	 *  只保留队列中没有加入队列的pdf文件。
	 * 2. 文件夹
	 *  保留文件夹中没有加入队列的pdf文件。
	 * @param files
	 * @return
	 */
	public List<File> filter(List<File> files) {
		List<File> pdfs = new ArrayList<File>();
		
		int len = files.size();
		for(int i=0; i<len; i++) {
			File file = files.get(i);
			
			if (file.isDirectory()) {
				// 获取文件夹中的pdf文件
				File[] subFiles = file.listFiles(new FileFilter() {
					public boolean accept(File f) {
						return isPdf(f) && !contains(f);
					}
				});
				
				if (subFiles != null && subFiles.length > 0) {
					for(File f: subFiles) {
						pdfs.add(f);
					}
				}
				
			} else if (!contains(file)){
				pdfs.add(file);
			}
		}
		
		return pdfs;
	}
	public synchronized void add(List<File> files) {
		if (files == null)
			return;
		
		int len = files.size();
		for (int i = 0; i < len; i++) {
			add(files.get(i));
		}
	}
	/**
	 * 添加文件
	 * @param files
	 */
	public synchronized void add(File[] files) {
		if (files == null)
			return;
		
		for (int i = 0; i < files.length; i++) {
			add(files[i]);
		}
	}
	

	/**
	 * 将文件添加到列表中
	 * @param file
	 */
	public synchronized void add(File file) {
		if (file.isDirectory()) {
			// 将整个文件夹中的文件全部添加到列表中
			add(file.listFiles());
		} else {
			String name = file.getName().toLowerCase();
			// 判断是否是pdf文件
			if (name.endsWith(".pdf")) {

				try {
					// 检查是否已经添加过此文件。
					if (contains(file))
						return;
					
					// 添加PDF文件
					PdfFile pdfFile = new PdfFile(file);
					this.add(pdfFile);
					data.add(pdfFile);
					
					logger.debug("Add new file: {}", file.getName());
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("ERROR adding new file: {} {}", file, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 获得
	 * @param file
	 * @return
	 */
	public PdfFile get(File file) {
		int row = size();
		for(int i=0; i<row; i++) {
			if (get(i).getFile().equals(file)) {
				return get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * 检查是否所有文件都被选中了
	 * @return
	 */
	public boolean allSelected() {
		int row = size();
		for(int i=0; i<row; i++) {
			if (!get(i).getSelect()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 全部选中/反选
	 * @param select
	 */
	public synchronized void selectedAll(boolean select) {
		int row = size();
		for(int i=0; i<row; i++) {
			if (select != get(i).getSelect()) {
				get(i).setSelect(select);
			}
		}
	}

	public void delete(PdfFile pdfFile) {
		if (contains(pdfFile)) {
			remove(pdfFile);
			data.remove(pdfFile);
		}
	}
}
