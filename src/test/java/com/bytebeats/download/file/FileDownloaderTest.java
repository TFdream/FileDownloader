package com.bytebeats.download.file;

import java.io.File;
import com.bytebeats.download.file.config.FileDownloaderConfiguration;

public class FileDownloaderTest {

	public static void main(String[] args) {
		
		FileDownloader fileDownloader = FileDownloader.getInstance();
		FileDownloaderConfiguration configuration = FileDownloaderConfiguration
				.newBuilder()
				.readTimeout(10*1000)
				.connectTimeout(10*1000)
				.corePoolSize(4)
				.downloadDestinationDir(new File("D:/Download"))
				.build();
		fileDownloader.init(configuration);
		
		String url = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";;
		String filename = "QQ7.9.exe";
		
		boolean result = fileDownloader.download(url, filename);
		
		System.out.println("download result:"+result);
		
		fileDownloader.destroy();	//close it when you not need
	}
}
