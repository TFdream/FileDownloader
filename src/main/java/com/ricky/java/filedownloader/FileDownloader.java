package com.ricky.java.filedownloader;

import org.apache.log4j.Logger;

import com.ricky.java.filedownloader.config.FileDownloaderConfiguration;

/**
 * Java 文件多线程下载
 * @author Ricky Fung
 *
 */
public class FileDownloader {
	
	protected Logger mLogger = Logger.getLogger("devLog");
	
	private volatile static FileDownloader fileDownloader;
	
	private FileDownloaderEngine downloaderEngine;
	
	private FileDownloaderConfiguration configuration;
	
	public static FileDownloader getInstance(){
		
		if(fileDownloader==null){
			synchronized (FileDownloader.class) {
				if(fileDownloader==null){
					fileDownloader = new FileDownloader();
				}
			}
		}
		
		return fileDownloader;
	}

	protected FileDownloader(){
	}
	
	public synchronized void init(FileDownloaderConfiguration configuration){
		if (configuration == null) {
			throw new IllegalArgumentException("FileDownloader configuration can not be initialized with null");
		}
		if (this.configuration == null) {
			mLogger.info("init FileDownloader");
			downloaderEngine = new FileDownloaderEngine(configuration);
			this.configuration = configuration;
		}else{
			mLogger.warn("Try to initialize FileDownloader which had already been initialized before.");
		}
		
	}
	
	public boolean download(String url, String filename){
		
		return downloaderEngine.download(url, filename);
	}
	
	public boolean isInited() {
		return configuration != null;
	}
	
	public void destroy() {
		if(downloaderEngine!=null){
			downloaderEngine.close();
			downloaderEngine = null;
		}
	}
}
