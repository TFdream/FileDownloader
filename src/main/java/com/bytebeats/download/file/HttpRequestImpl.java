package com.bytebeats.download.file;

import com.bytebeats.download.file.config.FileDownloaderConfiguration;
import com.bytebeats.download.file.exeception.RetryFailedException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class HttpRequestImpl {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int maxRetryCount;  
    private long requestBytesSize;  
    
	private OkHttpClient client;
	
	public HttpRequestImpl(FileDownloaderConfiguration configuration){

		maxRetryCount = configuration.getMaxRetryCount();
		requestBytesSize = configuration.getRequestBytesSize();

		this.client = new OkHttpClient.Builder()
				.connectTimeout(configuration.getConnectTimeout(), TimeUnit.MILLISECONDS)
				.readTimeout(configuration.getReadTimeout(), TimeUnit.MILLISECONDS)
				.writeTimeout(configuration.getWriteTimeout(), TimeUnit.MILLISECONDS)
				.build();
	}
	
	public void downloadPartFile(int id, String url, File file, long start, long end){
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rws");
		} catch (FileNotFoundException e) {
			logger.error("file not found:"+file, e);
			throw new IllegalArgumentException(e);
		}
		
		int retry = 0;
		long pos = start;
		while(pos<end){
			
			long end_index = pos + requestBytesSize;
			if(end_index>end){
				end_index = end;
			}
			
			boolean success = false;
			try {
				success = requestByRange(url, raf, pos, end_index);
			} catch (IOException e) {
				logger.error("download error,start:"+pos+",end:"+end_index, e);
			}catch (Exception e) {
				logger.error("download error,start:"+pos+",end:"+end_index, e);
			}
			
			if(success){
				pos += requestBytesSize;
				retry = 0;
			}else{
				if(retry < maxRetryCount){
					retry++;
					logger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,重试"+retry+"次");
				}else{
					logger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,放弃重试!");
					throw new RetryFailedException("超过最大重试次数");
				}
			}
		}
		
	}
	
	private boolean requestByRange(String url, RandomAccessFile raf, long start, long end) throws IOException {

		Request request = new Request.Builder()
				.url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36")
				.addHeader("Range", "bytes=" + start + "-" + end)
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		InputStream in = response.body().byteStream();
		raf.seek(start);// 设置保存数据的位置

		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1){
			raf.write(buffer, 0, len);
		}

		return true;
	}
	
	public long getFileSize(String url){
		
		int retry = 0;
		long fileSize = 0;
		while(retry<maxRetryCount){
			try {
				fileSize = getContentLength(url);
			} catch (Exception e) {
				logger.error("get File Size error", e);
			}

			if(fileSize>0){
				break;
			}else{
				retry++;
				logger.warn("get File Size failed,retry:"+retry);
			}
		}
		
		return fileSize;
	}
	
	private long getContentLength(String url) throws IOException{

		Request request = new Request.Builder()
				.url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36")
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);

		return response.body().contentLength();
	}
	
	public void close(){
		
		if(client!=null){
			try {
				client.connectionPool().evictAll();
				client.dispatcher().executorService().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			client = null;
		}
	}
}
