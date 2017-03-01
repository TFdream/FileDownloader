package com.bytebeats.download.file.job;

import com.bytebeats.download.file.HttpRequestImpl;
import com.bytebeats.download.file.exeception.RetryFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class DownloadWorker extends Worker {

	private final Logger mLogger = LoggerFactory.getLogger(this.getClass());
	
	private int id;
	private String url;
	private File file;
	private long thread_download_len;
	
	private CountDownLatch latch;
	
	private HttpRequestImpl httpRequestImpl;
	
	public DownloadWorker(int id, String url, long thread_download_len, File file, HttpRequestImpl httpRequestImpl, CountDownLatch latch) {
		this.id = id;
		this.url = url;
		this.thread_download_len = thread_download_len;
		this.file = file;
		this.httpRequestImpl = httpRequestImpl;
		this.latch = latch;
	}
	
	@Override
	public void run() {
		
		long start = id * thread_download_len;						// 起始位置
		long end = id * thread_download_len + thread_download_len - 1;		// 结束位置
		
		mLogger.info("线程:" + id +" 开始下载 url:"+url+ ",range:" + start + "-" + end);
		
		boolean result = false;
		try {
			httpRequestImpl.downloadPartFile(id, url, file, start, end);
			result = true;
			mLogger.info("线程:" + id + " 下载 "+url+ " range[" + start + "-" + end+"] 成功");
			
		} catch (RetryFailedException e) {
			mLogger.error("线程:" + id +" 重试出错", e);
		}catch (Exception e) {
			mLogger.error("线程:" + id +" 下载出错", e);
		}
		
		if(listener!=null){
			mLogger.info("notify FileDownloaderEngine download result");
			listener.notify(id, url, start, end, result, "");
		}
		
		latch.countDown();
	}

}
