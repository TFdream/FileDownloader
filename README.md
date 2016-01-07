# FileDownloader
File Downloader library for java and android,simple to use.
example:<br>
FileDownloader fileDownloader = FileDownloader.getInstance();
FileDownloaderConfiguration configuration = FileDownloaderConfiguration
		.custom()
		.coreThreadNum(5)
		.downloadDestinationDir(new File("D:/Download"))
		.build();
<br>
fileDownloader.init(configuration);
<br>
String url = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";
<br>
String filename = "QQ7.9.exe";
<br>
boolean result = fileDownloader.download(url, filename);
<br>
System.out.println("download result:"+result);
<br>
fileDownloader.destroy();	//close it when you not need
