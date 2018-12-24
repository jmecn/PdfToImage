package net.jmecn.toolkit.pdf;
/**
 * 输出文件夹所处位置
 * 
 * @author yanmaoyuan
 *
 */
public enum OutputModel {
	/**
	 * 输出到PdfToImage程序的output文件夹中。
	 */
	Local,
	/**
	 * 输出到pdf文件所在文件夹中。
	 */
	Relative;
}