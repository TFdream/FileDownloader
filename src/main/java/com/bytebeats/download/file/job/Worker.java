package com.bytebeats.download.file.job;

public abstract class Worker implements Runnable {
	
	protected DownloadListener listener;
	
	public void addListener(DownloadListener listener){
		this.listener = listener;
	}
	
	public interface DownloadListener{
		
		void notify(int thread_id, String url, long start, long end, boolean result, String msg);
	}
}
