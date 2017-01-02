# Ghostscript

主页：https://ghostscript.com/
下载：https://ghostscript.com/download/gsdnld.html

Ghostscript是一套建基于Adobe、PostScript及可移植文档格式（PDF）的页面描述语言等而编译成的免费软件。

Ghostscript最初是以商业软件形式在PC市场上发售，并称之为“GoScript”。但由于速度太慢（半小时一版A4），销量极差。后来有心人买下了版权，并改在Linux上开发，成为了今日的Ghostscript。

已经从Linux版本移植到其他操作系统，如其他Unix、Mac OS X、VMS、Windows、OS/2和Mac OS classic。

Ghostview 最早是由 L. Peter Deutsch和阿拉丁企业开发的，以Aladdin Free Public License（AFPL）发布，由artofcode LLC拥有并维护。推出了两个版本：一是在原来的AFPL许可下进行商业使用的AFPL Ghostscript，一是GNU General Public License 下使用的GPL Ghostscript。

GPL版本也是Display Ghostscript的基础，其增加了所需的功能，以便对 Display PostScript形成完全的支持。

# Ghost4J

Ghost4J binds the Ghostscript C API to bring Ghostscript power to the Java world. It also provides a high-level API to handle PDF and Postscript documents with objects.

* Home: http://www.ghost4j.org/
* Download: http://www.ghost4j.org/downloads.html

## issue 21

https://github.com/zippy1978/ghost4j/issues/21

使用Ghost4J需要安装GhostScript，因为它通过JNA调用GhostScript的库文件。

在Linux系统，依赖 libgs.so
在windows 64位，依赖 win32-x86-64/gsdll64.dll
在windows 32位，依赖 win32-x86-32/gsdll32.dll

这些文件可以通过下载安装GhostScript后，在其文件夹中找到。

可以通过下面的放置，指定dll文件所在的目录

	String path = "C:\Program Files\gs\gs9.07\bin";
	System.setProperty("jna.library.path", path);

## 内存

DPI 300，输出32页PDF时，已经消耗1GB内存，再继续生成png时都报内存溢出的异常。

	JNA: Callback org.ghost4j.Ghostscript$10@d44fc21 threw the following exception:
	java.lang.OutOfMemoryError: Java heap space

DPI 150，输出122页正常，第123页报Java heap space异常

# Dependency

## Maven

	<dependencies>
	    ...
	
	    <dependency>
	        <groupId>org.ghost4j</groupId>
	        <artifactId>ghost4j</artifactId>
	        <version>1.0.1</version>
	    </dependency>
	
	    ...
	</dependencies>

## Gradle

	dependencies {
		...
		
		compile 'org.ghost4j:ghost4j:1.0.1'
		
		...
	}