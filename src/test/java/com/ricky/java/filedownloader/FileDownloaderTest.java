package com.ricky.java.filedownloader;

import java.io.File;

import com.ricky.java.filedownloader.config.FileDownloaderConfiguration;

public class FileDownloaderTest {

	public static void main(String[] args) {
		
		FileDownloader fileDownloader = FileDownloader.getInstance();
		FileDownloaderConfiguration configuration = FileDownloaderConfiguration
				.custom()
				.coreThreadNum(5)
				.downloadDestinationDir(new File("D:/Download"))
				.build();
		fileDownloader.init(configuration);
		
		String url = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";;
		String filename = "QQ7.9.exe";
		
		boolean result = fileDownloader.download(url, filename);
		
		System.out.println("download result:"+result);
		
		fileDownloader.destroy();
	}
}
