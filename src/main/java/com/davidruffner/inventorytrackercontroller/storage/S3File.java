package com.davidruffner.inventorytrackercontroller.storage;

import com.davidruffner.inventorytrackercontroller.config.StorageConfig;
import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.INTERNAL_ERROR;

public class S3File {
    private String fileName;
    private String bucketName;
    private byte[] bytes;
    private long fileSize;
    private String fileExtension;
    private Map<String, String> metadata;

    public S3File(String keyName, String fileExtension, String bucketName,
                  byte[] bytes, StorageConfig.Bucket bucketConfig) throws ControllerException {
        this.fileName = keyName;
        this.bucketName = bucketName;
        this.bytes = bytes;

        if (bucketConfig.isFileTooLarge(bytes.length)) {
            throw new ControllerException.Builder(BAD_REQUEST, this.getClass())

                    .build();
        }

        this.fileSize = bytes.length;
    }

    public S3File(File tmpFile, String bucketName, Map<String, String> metadata)
            throws ControllerException {
        String[] nameParts = tmpFile.getName().split("\\.");

        this.fileName = nameParts[0];
        this.fileExtension = nameParts[1];
        this.bucketName = bucketName;
        this.metadata = metadata;

        try (FileInputStream fis = new FileInputStream(tmpFile)) {
            this.bytes = fis.readAllBytes();
            this.fileSize = tmpFile.length();
        } catch (Exception ex) {
            throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                    .withErrorMessage(ex.getMessage())
                    .withInternalErrorResponseMessage()
                    .build();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public S3File setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public S3File setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public S3File setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public long getFileSize() {
        return fileSize;
    }

    public S3File setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public S3File setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }

    public boolean hasMetadata() {
        return !this.metadata.isEmpty();
    }

    public Map<String, String> getAllMetadata() {
        return metadata;
    }

    public S3File setAllMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getMetadata(String key) throws ControllerException {
        try {
            return metadata.get(key);
        } catch (Exception ex) {
            throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                    .withErrorMessage(ex.getMessage())
                    .withInternalErrorResponseMessage()
                    .build();
        }
    }

    public void setOrAddMetadata(String key, String value) throws ControllerException {
        try {
            this.metadata.put(key, value);
        } catch (Exception ex) {
            throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                    .withErrorMessage(ex.getMessage())
                    .withInternalErrorResponseMessage()
                    .build();
        }
    }

    public void removeMetadata(String key) throws ControllerException {
        try {
            this.metadata.remove(key);
        } catch (Exception ex) {
            throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                    .withErrorMessage(ex.getMessage())
                    .withInternalErrorResponseMessage()
                    .build();
        }
    }

    public void removeAllMetadata() {
        this.metadata = new HashMap<>();
    }
}
