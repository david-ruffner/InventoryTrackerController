package com.davidruffner.inventorytrackercontroller.storage;

import com.davidruffner.inventorytrackercontroller.config.StorageConfig;
import com.davidruffner.inventorytrackercontroller.config.StorageConfig.BUCKET_PREFIX;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.config.StorageConfig.BUCKET_PREFIX.PICTURES;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.INTERNAL_ERROR;

@Service
public class S3StorageService {
    private final StorageConfig storageConfig;

    public S3StorageService(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    public S3File getFile(String keyName, BUCKET_PREFIX bucketPrefix) throws ControllerException {
        StorageConfig.Bucket bucketConfig = storageConfig.getBucketConfig(bucketPrefix);
        S3Client s3Client = storageConfig.getS3Client();

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketConfig.getBucketName())
                .key(keyName)
                .build();

        try {
            Path tmpOutputPath = bucketConfig.getTempStoragePath(keyName);
            if (Files.exists(tmpOutputPath)) {
                Files.delete(tmpOutputPath);
            }

            GetObjectResponse getResponse = s3Client.getObject(getRequest, tmpOutputPath);
            File tmpFile = bucketConfig.getTempFile(tmpOutputPath);
            s3Client.close();

            return new S3File(tmpFile, bucketConfig.getBucketName(),
                    getResponse.metadata());
        } catch (Exception ex) {
            throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                    .withErrorMessage(ex.getMessage())
                    .withInternalErrorResponseMessage()
                    .build();
        }
    }

    public void uploadFile(S3File s3File, BUCKET_PREFIX bucketPrefix) throws ControllerException {
        StorageConfig.Bucket bucketConfig = storageConfig.getBucketConfig(bucketPrefix);
        S3Client s3Client = storageConfig.getS3Client();
        PutObjectRequest.Builder putRequest = PutObjectRequest.builder()
                .bucket(bucketConfig.getBucketName())
                .key("test_file");

        if (s3File.hasMetadata())
            putRequest.metadata(s3File.getAllMetadata());
    }
}
