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
	
	final static String RESOLUTION = "Resolution";
	final static String _DPI = "DPI";
	final static String OUTPUT_MODEL = "OutputModel";
	final static String OUTPUT_FOLDER = "OutputFolder";
	final static String LAST_PATH = "lastPath";

	final static Settings settings = new Settings();

	/**
	 * 上次打开窗口时选择的文件路径
	 */
	private String lastPath = "./";

	// 输出文件夹
	private OutputModel model;
	private String output = "./output";

	// 分辨率
	private DPI resolution;
	private int dpi;

	private Settings() {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		
		// default resolution
		String res = pref.get(RESOLUTION, "");
		if (res.equals("")) {
			res = DPI.ldpi.toString();
			pref.put(RESOLUTION, res);
		}
		resolution = DPI.valueOf(res);
		
		// default DPI
		int tmp = pref.getInt(_DPI, -1);
		if (tmp == -1) {
			tmp = DPI.ldpi.dpi;
			pref.putInt(_DPI, tmp);
		}
		dpi = tmp;
		
		// default OutputModel
		String outputModel = pref.get(OUTPUT_MODEL, "");
		if (outputModel.equals("")) {
			outputModel = OutputModel.Local.toString();
			pref.put(OUTPUT_MODEL, outputModel);
		}
		model = OutputModel.valueOf(outputModel);
		
		// default OutputFolder
		output = pref.get(OUTPUT_FOLDER, "");
		if (output.equals("")) {
			output = "./output";
			pref.put(OUTPUT_FOLDER, output);
		}
		
	}

	public static Settings get() {
		return settings;
	}

	public void setLastPath(String lastPath) {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.put(LAST_PATH, lastPath);
		this.lastPath = lastPath;
	}
	
	public String getLastPath() {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		lastPath = pref.get(LAST_PATH, "./");
		return lastPath;
	}

	public void setResolution(DPI dpi) {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.put(RESOLUTION, dpi.toString());
		this.resolution = dpi;
	}

	public DPI getResolution() {
		return resolution;
	}
	
	public void setDpi(int dpi) {
		setResolution(DPI.custom);
		
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.putInt(_DPI, dpi);
		this.dpi = dpi;
	}
	
	public int getDpi() {
		if (resolution != DPI.custom) {
			return resolution.dpi;
		} else {
			return dpi;
		}
	}

	public void setOutputModel(OutputModel model) {
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.put(OUTPUT_MODEL, model.toString());
		this.model = model;
	}
	
	public OutputModel getOutputModel() {
		return model;
	}

	public void setOutput(String output) {
		setOutputModel(OutputModel.Absolute);
		
		Preferences pref = Preferences.userRoot().node(REGISTRY_KEY);
		pref.put(OUTPUT_FOLDER, output);
		this.output = output;
	}

	public String getOutput() {
		return output;
	}
}
