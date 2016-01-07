package com.ricky.java.filedownloader.job;

public abstract class Worker implements Runnable {
	
	protected DownloadListener listener;
	
	public void addListener(DownloadListener listener){
		this.listener = listener;
	}
	
	public interface DownloadListener{
		
		public void notify(int thread_id, String url, long start, long end, boolean result, String msg);
	}
}
