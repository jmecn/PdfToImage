PdfToImage
==========

本项目用于将PDF文件转换为png图片，并根据书签生成超链接，便于在浏览器中直接阅读。

运行环境：Java 8 (Java FX 8)

主要依赖：Ghostscript、ghost4j、itextpdf。


## 历史更新

### 找不到gsdll64.dll

https://github.com/zippy1978/ghost4j/issues/21

使用Ghost4J需要安装GhostScript，因为它通过JNA调用GhostScript的库文件。

在Linux系统，依赖 libgs.so

在windows 64位，依赖 win32-x86-64/gsdll64.dll

在windows 32位，依赖 win32-x86-32/gsdll32.dll

这些文件可以通过下载安装GhostScript后，在其文件夹中找到。

可以通过下面的放置，指定dll文件所在的目录

	String path = "C:\Program Files\gs\gs9.07\bin";
	System.setProperty("jna.library.path", path);

解决方案：搜集gsdll，打包为ghost4j-native-1.0.2.jar文件，直接添加到项目依赖。

### 内存溢出

DPI 300，输出32页PDF时，已经消耗1GB内存，再继续生成png时都报内存溢出的异常。

	JNA: Callback org.ghost4j.Ghostscript$10@d44fc21 threw the following exception:
	java.lang.OutOfMemoryError: Java heap space

DPI 150，输出122页正常，第123页报Java heap space异常

建议解决方案：

1. 逐页转换。
2. 增加分辨率选项，使用更小的分辨率。

### unkown rValue = 5

Ghost4j目前采用的itext 2.1.7，在解读加密pdf文件时，最高只能处理128 bit加密(rValue = 4)。当处理使用256 bit加密的PDF文件时，将会产生异常: `xxx is not a valid pdf file.`

解决方案：itext项目已经改名为itextpdf，并且升级到了5.5.10。我对ghost4j的源码采用itextpdf进行移植，并重新编译为ghost4j-1.0.2.jar。

## Ghostscript

主页：https://ghostscript.com/

下载：https://ghostscript.com/download/gsdnld.html

Ghostscript是一套建基于Adobe、PostScript及可移植文档格式（PDF）的页面描述语言等而编译成的免费软件。

Ghostscript最初是以商业软件形式在PC市场上发售，并称之为“GoScript”。但由于速度太慢（半小时一版A4），销量极差。后来有心人买下了版权，并改在Linux上开发，成为了今日的Ghostscript。

已经从Linux版本移植到其他操作系统，如其他Unix、Mac OS X、VMS、Windows、OS/2和Mac OS classic。

Ghostview 最早是由 L. Peter Deutsch和阿拉丁企业开发的，以Aladdin Free Public License（AFPL）发布，由artofcode LLC拥有并维护。推出了两个版本：一是在原来的AFPL许可下进行商业使用的AFPL Ghostscript，一是GNU General Public License 下使用的GPL Ghostscript。

GPL版本也是Display Ghostscript的基础，其增加了所需的功能，以便对 Display PostScript形成完全的支持。

## Ghost4J

Ghost4J binds the Ghostscript C API to bring Ghostscript power to the Java world. It also provides a high-level API to handle PDF and Postscript documents with objects.

* Home: http://www.ghost4j.org/
* Download: http://www.ghost4j.org/downloads.html

重新集成itextpdf的ghost4j-1.0.2

https://github.com/jmecn/Ghost4j