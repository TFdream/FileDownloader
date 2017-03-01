package com.bytebeats.download.file.config;

import java.io.File;

public class FileDownloaderConfiguration {
	private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private final int maxRetryCount;
    private final int corePoolSize;
    private final long requestBytesSize;
    private final File downloadDestinationDir;  
    
    private FileDownloaderConfiguration(Builder builder) {  
        this.connectTimeout = builder.connectTimeout;  
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.maxRetryCount = builder.maxRetryCount;  
        this.corePoolSize = builder.corePoolSize;
        this.requestBytesSize = builder.requestBytesSize;
        this.downloadDestinationDir = builder.downloadDestinationDir;  
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public int getMaxRetryCount() {
		return maxRetryCount;
	}
	public int getCorePoolSize() {
		return corePoolSize;
	}
	public long getRequestBytesSize() {
		return requestBytesSize;
	}
	public File getDownloadDestinationDir() {
		return downloadDestinationDir;
	}

    public static FileDownloaderConfiguration.Builder newBuilder() {
        return new Builder();  
    }
    
    public static class Builder {  
        private int connectTimeout;  
        private int readTimeout;
        private int writeTimeout;
        private int maxRetryCount;  
        private int corePoolSize;
        private long requestBytesSize;  
        private File downloadDestinationDir;  
        
        public Builder connectTimeout(int connectTimeout) {  
            this.connectTimeout = connectTimeout;  
            return this;  
        }  
        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        public Builder writeTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }
        public Builder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
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
                builder.connectTimeout = 5*1000;
            }  
              
            if(builder.readTimeout<1){
                builder.readTimeout = 5*1000;
            }
            if(builder.writeTimeout<1){
                builder.writeTimeout = 5*1000;
            }
            if(builder.maxRetryCount<1){
                builder.maxRetryCount = 1;  
            }  
            if(builder.corePoolSize<1){
                builder.corePoolSize = 4;
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
