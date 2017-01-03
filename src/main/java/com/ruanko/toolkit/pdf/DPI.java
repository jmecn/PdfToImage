package com.ruanko.toolkit.pdf;

/**
 * 分辨率
 * 
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