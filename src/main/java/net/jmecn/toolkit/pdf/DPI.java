package net.jmecn.toolkit.pdf;

/**
 * 分辨率
 * 
 * @author Administrator
 *
 */
public enum DPI {
	ldpi(120), mdpi(160), hdpi(240), xhdpi(320), xxhdpi(480);
	int dpi;

	private DPI(int dpi) {
		this.dpi = dpi;
	}
}