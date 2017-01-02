package com.ruanko.toolkit.pdf;

/**
 * 应用程序设置
 * 
 * @author yanmaoyuan
 *
 */
public class Settings {

	/**
	 * 输出文件夹所处位置
	 * @author yanmaoyuan
	 *
	 */
	public enum OutputFolder {
		/**
		 * 输出到Pdf2Image程序的output文件夹中。
		 */
		Local,
		/**
		 * 输出到pdf文件所在文件夹中。
		 */
		Relative,
		/**
		 * 用户指定文件夹
		 */
		Absolute;
	}
	
	// 输出文件夹
	private OutputFolder model = OutputFolder.Local;
	private String output = "./output";
	
	/**
	 * 分辨率
	 * @author Administrator
	 *
	 */
	public enum DPI {
		ldpi(120), mdpi(160), hdpi(240), xdpi(320), xxdpi(480), custom(-1);
		int dpi;

		private DPI(int dpi) {
			this.dpi = dpi;
		}
	}

	// 分辨率
	private DPI resolution = DPI.hdpi;
	private int dpi = 300;
	
	public Settings() {
		
	}
	
	public void setDpi(DPI dpi) {
		this.resolution = dpi;
	}
	
	public void setDpi(int dpi) {
		this.resolution = DPI.custom;
		this.dpi = dpi;
	}
	
	public int getDpi() {
		if (resolution != DPI.custom) {
			return resolution.dpi;
		} else {
			return dpi;
		}
	}
}
