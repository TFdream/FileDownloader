package com.bytebeats.download.file;

import com.bytebeats.download.file.config.FileDownloaderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java 文件多线程下载
 * @author Ricky Fung
 *
 */
public class FileDownloader {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private FileDownloaderEngine downloaderEngine;

	private FileDownloaderConfiguration configuration;

	private FileDownloader(){
	}

	public static FileDownloader getInstance(){
		return Singleton.INSTANCE;
	}

	public synchronized void init(FileDownloaderConfiguration configuration){
		if (configuration == null) {
			throw new IllegalArgumentException("FileDownloader configuration can not be initialized with null");
		}
		if (this.configuration == null) {
			logger.info("init FileDownloader");
			downloaderEngine = new FileDownloaderEngine(configuration);
			this.configuration = configuration;
		}else{
			logger.warn("Try to initialize FileDownloader which had already been initialized before.");
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

	private static class Singleton {
		private static final FileDownloader INSTANCE = new FileDownloader();
	}
}
