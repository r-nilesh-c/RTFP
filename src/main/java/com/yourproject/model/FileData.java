package com.yourproject.model;

public class FileData {
    private byte[] content;
    private String fileName;
    private String contentType;
    private long timestamp;

    public FileData(byte[] content, String fileName, String contentType, long timestamp) {
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
        this.timestamp = timestamp;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
