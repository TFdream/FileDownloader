package com.ricky.java.filedownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.ricky.common.java.http.HttpClientManager;
import com.ricky.java.filedownloader.config.FileDownloaderConfiguration;
import com.ricky.java.filedownloader.exeception.RetryFailedException;

public class HttpRequestImpl {

	protected Logger mLogger = Logger.getLogger("devLog");
	
	private int connectTimeout;  
    private int socketTimeout;  
    private int maxRetryCount;  
    private long requestBytesSize;  
    
	private CloseableHttpClient httpclient = HttpClientManager.getHttpClient();
	
	public HttpRequestImpl(FileDownloaderConfiguration configuration){
		connectTimeout = configuration.getConnectTimeout();
		socketTimeout = configuration.getSocketTimeout();
		maxRetryCount = configuration.getMaxRetryCount();
		requestBytesSize = configuration.getRequestBytesSize();
	}
	
	public void downloadPartFile(int id, String url, File file, long start, long end){
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rws");
		} catch (FileNotFoundException e) {
			mLogger.error("file not found:"+file, e);
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
			} catch (ClientProtocolException e) {
				mLogger.error("download error,start:"+pos+",end:"+end_index, e);
			}catch (IOException e) {
				mLogger.error("download error,start:"+pos+",end:"+end_index, e);
			}catch (Exception e) {
				mLogger.error("download error,start:"+pos+",end:"+end_index, e);
			}
			
//			mLogger.info("线程:" + id +",download url:"+url+",range:"+ pos + "-" + end_index+",success="+success );
			
			if(success){
				pos += requestBytesSize;
				retry = 0;
			}else{
				if(retry < maxRetryCount){
					retry++;
					mLogger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,重试"+retry+"次");
				}else{
					mLogger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,放弃重试!");
					throw new RetryFailedException("超过最大重试次数");
				}
			}
		}
		
	}
	
	private boolean requestByRange(String url, RandomAccessFile raf, long start, long end) throws ClientProtocolException, IOException {
		
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		httpget.setHeader("Range", "bytes=" + start + "-" + end);
		
		RequestConfig requestConfig = RequestConfig.custom()
        		.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout)
				.build();
		
        httpget.setConfig(requestConfig);

        CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		    
	        int code = response.getStatusLine().getStatusCode();
	        
	        if(code==HttpStatus.SC_OK || code== HttpStatus.SC_PARTIAL_CONTENT){
	        	
                HttpEntity entity = response.getEntity();
                
                if (entity != null) {
                	
                	InputStream in = entity.getContent();
    				raf.seek(start);// 设置保存数据的位置

    				byte[] buffer = new byte[1024];
    				int len;
    				while ((len = in.read(buffer)) != -1){
    					raf.write(buffer, 0, len);
    				}
    				
    				return true;
                }else{
                	mLogger.warn("response entity is null,url:"+url);
                }
	        }else{
	        	mLogger.warn("response error, code="+code+",url:"+url);
	        }
		}finally {
			IOUtils.closeQuietly(response);
        }
		
		return false;
	}
	
	public long getFileSize(String url){
		
		int retry = 0;
		long filesize = 0;
		while(retry<maxRetryCount){
			try {
				filesize = getContentLength(url);
			} catch (Exception e) {
				mLogger.error("get File Size error", e);
			}

			if(filesize>0){
				break;
			}else{
				retry++;
				mLogger.warn("get File Size failed,retry:"+retry);
			}
		}
		
		return filesize;
	}
	
	private long getContentLength(String url) throws ClientProtocolException, IOException{
		
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		
		RequestConfig requestConfig = RequestConfig.custom()
        		.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout)
				.build();
		
        httpget.setConfig(requestConfig);

        CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		    
	        int code = response.getStatusLine().getStatusCode();
	        
	        if(code==HttpStatus.SC_OK){
	        	
                HttpEntity entity = response.getEntity();
                
                if (entity != null) {
                	return entity.getContentLength();
                }
	        }else{
	        	mLogger.warn("response code="+code);
	        }
	        
		}finally {
            IOUtils.closeQuietly(response);
        }
		
		return -1;
	}
	
	public void close(){
		
		if(httpclient!=null){
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpclient = null;
		}
	}
}
