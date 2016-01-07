# FileDownloader
<p>File Downloader library for java and android,simple to use.</p>
example:
```java
<p>
FileDownloader fileDownloader = FileDownloader.getInstance(); 
FileDownloaderConfiguration configuration = FileDownloaderConfiguration
	.custom().coreThreadNum(5)
	.downloadDestinationDir(new File("D:/Download")) 
	.build(); 
	
fileDownloader.init(configuration); 

String url = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe"; 
String filename = "QQ7.9.exe"; 
boolean result = fileDownloader.download(url, filename); 
System.out.println("download result:"+result); 
fileDownloader.destroy(); //close it when you not need
</p>
```
