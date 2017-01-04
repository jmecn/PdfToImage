package com.ruanko.toolkit.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

/**
 * 读取文件目录
 * @author yanmaoyuan
 *
 */
public class TestPdf {

	public static void main(String[] args) throws Exception {
		PdfReader reader = new PdfReader("Ace_BlueBook.pdf");
		List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);

		if (list == null) {
			System.out.println("无书签");
		} else {
			for (Iterator<HashMap<String, Object>> i = list.iterator(); i.hasNext();) {
	
				showBookmark(i.next(), 0);
	
			}
		}
	}

	// 获取标题
	private static void showBookmark(HashMap<String, Object> bookmark, int depth) {
		System.out.println(" " +  bookmark.keySet() + " " + bookmark.get("Named") + " ");
		
		for(int i=0; i<depth; i++) {
			System.out.print("\\");
		}
		
		// 获取标题
		
		if ("GoTo".equals(bookmark.get("Action"))) {
			
			System.out.print(bookmark.get("Title"));

			// 获取页码
			String page = (String) bookmark.get("Page");
			if (page != null) {

				page = page.trim();

				int idx = page.indexOf(' ');

				int pageNum;

				if (idx < 0) {

					pageNum = Integer.parseInt(page);
					System.out.print(" ------------------- " + pageNum);
				} else {

					pageNum = Integer.parseInt(page.substring(0, idx));
					System.out.print(" ------------------- " + pageNum);
				}
			}
		}
		
		System.out.println();
		
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> kids = (ArrayList<HashMap<String, Object>>) bookmark.get("Kids");
		if (kids == null)
			return;
		for (Iterator<HashMap<String, Object>> i = kids.iterator(); i.hasNext();) {

			showBookmark(i.next(), depth+1);
		}
	}

}