package com.bytebeats.download.file;

import com.bytebeats.download.file.config.FileDownloaderConfiguration;
import com.bytebeats.download.file.job.DownloadWorker;
import com.bytebeats.download.file.job.Worker.DownloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileDownloaderEngine {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private FileDownloaderConfiguration configuration;
	
	private ThreadPoolExecutor pool;
	
	private HttpRequestImpl httpRequestImpl;
	
	private File downloadDestinationDir;
	
	private int corePoolSize;
	
	public FileDownloaderEngine(FileDownloaderConfiguration configuration){
		
		this.configuration = configuration;
		
		this.corePoolSize = configuration.getCorePoolSize();
		this.httpRequestImpl = new HttpRequestImpl(this.configuration);
		this.pool = new ThreadPoolExecutor(this.corePoolSize,
				this.corePoolSize+1, 5, TimeUnit.MINUTES, new LinkedBlockingQueue());
		
		this.downloadDestinationDir = this.configuration.getDownloadDestinationDir();
		if(!this.downloadDestinationDir.exists()){
			this.downloadDestinationDir.mkdirs();
		}
	}
	
	public boolean download(String url, String filename){
		
		long start_time = System.currentTimeMillis();
		logger.info("开始下载,url:"+url+",filename:"+filename);
		
		long total_file_len = httpRequestImpl.getFileSize(url);						// 获取文件长度
		
		if(total_file_len<1){
			logger.warn("获取文件大小失败,url:"+url+",filename:"+filename);
			return false;
		}
		
		final BitSet downloadIndicatorBitSet = new BitSet(corePoolSize);	//标记每个线程下载是否成功
		
		File file = null;
		try {
			
			file = new File(downloadDestinationDir, filename);
			
			RandomAccessFile raf = new RandomAccessFile(file, "rws");			// 在本地创建一个和服务端大小相同的文件
			raf.setLength(total_file_len);											// 设置文件的大小
			raf.close();

			logger.info("create new file:"+file);
			
		} catch (FileNotFoundException e) {
			logger.error("create new file error", e);
		} catch (IOException e) {
			logger.error("create new file error", e);
		}
		
		if(file==null || !file.exists()){
			logger.warn("创建文件失败,url:"+url+",filename:"+filename);
			return false;
		}
		
		long thread_download_len = (total_file_len + corePoolSize - 1) / corePoolSize;			// 计算每个线程要下载的长度

		logger.info("filename:"+filename+",total_file_len="+total_file_len+",coreThreadNum:"+corePoolSize+",thread_download_len:"+thread_download_len);
		
		CountDownLatch latch = new CountDownLatch(corePoolSize);//两个工人的协作
		
		for (int i = 0; i < corePoolSize; i++){
			
			DownloadWorker worker = new DownloadWorker(i, url, thread_download_len, file, httpRequestImpl, latch);
			worker.addListener(new DownloadListener() {
				
				@Override
				public void notify(int thread_id, String url, long start, long end,
						boolean result, String msg) {

					logger.info("thread_id:"+thread_id+" download result:"+result+",url->"+url);
					
					modifyState(downloadIndicatorBitSet, thread_id);
				}
			});
			
			pool.execute(worker);
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("CountDownLatch Interrupt", e);
		}

		logger.info("下载结束,url:"+url+",耗时:"+((System.currentTimeMillis()-start_time)/1000)+"(s)");
		
		return downloadIndicatorBitSet.cardinality()==corePoolSize;
	}
	
	private synchronized void modifyState(BitSet bitSet, int index){
		bitSet.set(index);
	}
	
	/**释放资源*/
	public void close(){
		
		if(httpRequestImpl!=null){
			httpRequestImpl.close();
			httpRequestImpl = null;
		}
		if(pool!=null){
			pool.shutdown();
			pool = null;
		}
		
	}
	
}
