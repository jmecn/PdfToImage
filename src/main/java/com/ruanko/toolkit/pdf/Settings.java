package com.ruanko.toolkit.pdf;

import java.util.prefs.Preferences;

/**
 * 应用程序设置
 * 
 * @author yanmaoyuan
 *
 */
public class Settings {
	final static String REGISTRY_KEY = "/com/ruanko/toolkit/pdf";

	final static Settings settings = new Settings();

	/**
	 * 上次打开窗口时选择的文件路径
	 */
	private String lastPath = "./";

	// 输出文件夹
	private OutputModel model = OutputModel.Local;
	private String output = "./output";

	// 分辨率
	private DPI resolution = DPI.hdpi;
	private int dpi = 300;

	private Settings() {
	}

	public static Settings get() {
		return settings;
	}

	public void setLastPath(String lastPath) {
		// 记忆用户最后选择的文件路径。
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.put("lastPath", lastPath);
		this.lastPath = lastPath;
	}
	
	public String getLastPath() {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		lastPath = pref.get("lastPath", "./");
		return lastPath;
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

	public void setOuputModel(OutputModel model) {
		this.model = model;
	}

	public void setOutput(String output) {
		this.model = OutputModel.Absolute;
		this.output = output;
	}

	public OutputModel getOutputModel() {
		return model;
	}

	public String getOutput() {
		return output;
	}
}
