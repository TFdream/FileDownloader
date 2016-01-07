package com.ricky.java.filedownloader.config;

import java.io.File;

public class FileDownloaderConfiguration {
	private final int connectTimeout;
    private final int socketTimeout;
    private final int maxRetryCount;
    private final int coreThreadNum;  
    private final long requestBytesSize;
    private final File downloadDestinationDir;  
    
    private FileDownloaderConfiguration(Builder builder) {  
        this.connectTimeout = builder.connectTimeout;  
        this.socketTimeout = builder.socketTimeout;  
        this.maxRetryCount = builder.maxRetryCount;  
        this.coreThreadNum = builder.coreThreadNum;  
        this.requestBytesSize = builder.requestBytesSize;
        this.downloadDestinationDir = builder.downloadDestinationDir;  
    }
    
    public int getConnectTimeout() {
		return connectTimeout;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}
	public int getMaxRetryCount() {
		return maxRetryCount;
	}
	public int getCoreThreadNum() {
		return coreThreadNum;
	}
	public long getRequestBytesSize() {
		return requestBytesSize;
	}
	public File getDownloadDestinationDir() {
		return downloadDestinationDir;
	}

    public static FileDownloaderConfiguration.Builder custom() {  
        return new Builder();  
    }
    
    public static class Builder {  
        private int connectTimeout;  
        private int socketTimeout;  
        private int maxRetryCount;  
        private int coreThreadNum;  
        private long requestBytesSize;  
        private File downloadDestinationDir;  
        
        public Builder connectTimeout(int connectTimeout) {  
            this.connectTimeout = connectTimeout;  
            return this;  
        }  
        public Builder socketTimeout(int socketTimeout) {  
            this.socketTimeout = socketTimeout;  
            return this;
        }  
        public Builder coreThreadNum(int coreThreadNum) {  
            this.coreThreadNum = coreThreadNum;  
            return this;  
        }  
        public Builder maxRetryCount(int maxRetryCount) {  
            this.maxRetryCount = maxRetryCount;  
            return this;  
        }  
        public Builder requestBytesSize(long requestBytesSize) {  
            this.requestBytesSize = requestBytesSize;  
            return this;  
        }  
        public Builder downloadDestinationDir(File downloadDestinationDir) {  
            this.downloadDestinationDir = downloadDestinationDir;  
            return this;  
        }
        
        public FileDownloaderConfiguration build() {  
            
            initDefaultValue(this);  
              
            return new FileDownloaderConfiguration(this);  
        }  
        
        private void initDefaultValue(Builder builder) {  
              
            if(builder.connectTimeout<1){  
                builder.connectTimeout = 6*1000;  
            }  
              
            if(builder.socketTimeout<1){  
                builder.socketTimeout = 6*1000;  
            }
            if(builder.maxRetryCount<1){
                builder.maxRetryCount = 1;  
            }  
            if(builder.coreThreadNum<1){  
                builder.coreThreadNum = 3;  
            }
            if(builder.requestBytesSize<1){  
                builder.requestBytesSize = 1024*128;  
            }
            if(builder.downloadDestinationDir==null){  
                builder.downloadDestinationDir = new File("./");  
            }
        }  
    }  
}
