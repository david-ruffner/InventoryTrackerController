package com.davidruffner.inventorytrackercontroller.config;

import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.INTERNAL_ERROR;

@ConfigurationProperties(prefix = "storage")
public class StorageConfig {
    public enum BUCKET_PREFIX {
        PICTURES("pictures");

        private String strVal;
        private static Map<String, BUCKET_PREFIX> stringToPrefix = new HashMap<>();

        static {
            for (BUCKET_PREFIX p : BUCKET_PREFIX.values()) {
                stringToPrefix.put(p.getStringValue(), p);
            }
        }

        BUCKET_PREFIX(String strVal) {
            this.strVal = strVal;
        }

        public String getStringValue() {
            return this.strVal;
        }

        public static BUCKET_PREFIX fromString(String strVal) throws ControllerException {
            try {
                return stringToPrefix.get(strVal);
            } catch (Exception ex) {
                throw new ControllerException.Builder(INTERNAL_ERROR, StorageConfig.class)
                        .withErrorMessage(ex.getMessage())
                        .build();
            }
        }
    }

    private final List<Bucket> buckets;
    private final AWS aws;

    public StorageConfig(List<Bucket> buckets, AWS aws) {
        this.aws = aws;
        this.buckets = buckets;
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(aws.getRegion()))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey())))
                .endpointOverride(URI.create(aws.getEndpointURL()))
                .forcePathStyle(true)
                .build();
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public Bucket getBucketConfig(BUCKET_PREFIX bucketPrefix) {
        return this.buckets.stream().filter(bucket ->
                bucket.bucketPrefix.equals(bucketPrefix)).toList().getFirst();
    }

    public AWS getAws() {
        return aws;
    }

    public static class Bucket {
        public enum SIZE_SUFFIX {
            KB("KB"),
            MB("MB"),
            GB("GB");

            private String strVal;
            private static Map<String, SIZE_SUFFIX> stringToSize = new HashMap<>();
            private static Map<SIZE_SUFFIX, Integer> sizeToInt;

            static {
                for (SIZE_SUFFIX s : SIZE_SUFFIX.values()) {
                    stringToSize.put(s.getStringValue(), s);
                }

                sizeToInt = Map.ofEntries(
                        Map.entry(KB, 1024),
                        Map.entry(MB, 1048576),
                        Map.entry(GB, 1073741824)
                );
            }

            SIZE_SUFFIX(String strVal) {
                this.strVal = strVal;
            }

            public String getStringValue() {
                return this.strVal;
            }

            public static Integer getSizeIntValue(SIZE_SUFFIX sizeSuffix) throws ControllerException {
                try {
                    return sizeToInt.get(sizeSuffix);
                } catch (Exception ex) {
                    throw new ControllerException.Builder(INTERNAL_ERROR, StorageConfig.class)
                            .withErrorMessage(ex.getMessage())
                            .build();
                }
            }

            public static SIZE_SUFFIX fromString(String strVal) throws ControllerException {
                try {
                    return stringToSize.get(strVal);
                } catch (Exception ex) {
                    throw new ControllerException.Builder(INTERNAL_ERROR, StorageConfig.class)
                            .withErrorMessage(ex.getMessage())
                            .build();
                }
            }
        }

        private final BUCKET_PREFIX bucketPrefix;
        private final String bucketName;
        private final long maxByteSize;
        private final String tempStorageDirectory;
        private final List<String> allowedFileTypes;

        public Bucket(String prefix, String bucketName, String maxFileSize,
                      String tempStorageDirectory, List<String> allowedFileTypes)
                throws ControllerException {
            this.bucketPrefix = BUCKET_PREFIX.fromString(prefix);
            this.bucketName = bucketName;
            this.tempStorageDirectory = tempStorageDirectory;
            this.allowedFileTypes = allowedFileTypes;

            Pattern MAX_FILE_SIZE_PATT = Pattern.compile("^(\\d+)_([A-Z]{2})$");
            if (!MAX_FILE_SIZE_PATT.matcher(maxFileSize).matches()) {
                maxByteSize = 0;
                throw new ControllerException.Builder(INTERNAL_ERROR, StorageConfig.class)
                        .withErrorMessage(String.format("Max file size: %s is invalid." +
                                " It must be like '100_MB' where MB can be KB, MB, or GB.",
                                maxFileSize))
                        .build();
            } else {
                try {
                    String[] maxFileSizeStrParts = maxFileSize.split("_");
                    int maxFileSizeNum = Integer.parseInt(maxFileSizeStrParts[0]);
                    SIZE_SUFFIX maxFileSizeSuffix =
                            SIZE_SUFFIX.fromString(maxFileSizeStrParts[1]);
                    int fileSizeMultiplier = SIZE_SUFFIX.getSizeIntValue(maxFileSizeSuffix);
                    this.maxByteSize = (long) maxFileSizeNum * fileSizeMultiplier;
                } catch (ControllerException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new ControllerException.Builder(INTERNAL_ERROR, StorageConfig.class)
                            .withErrorMessage(ex.getMessage())
                            .build();
                }
            }
        }

        public BUCKET_PREFIX getPrefix() {
            return bucketPrefix;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getTempStorageDirectory() {
            return tempStorageDirectory;
        }

        public Path getTempStoragePath(String keyName) throws ControllerException {
            try {
                return Paths.get(this.tempStorageDirectory).resolve(keyName);
            } catch (Exception ex) {
                throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                        .withErrorMessage(ex.getMessage())
                        .withInternalErrorResponseMessage()
                        .build();
            }
        }

        public File getTempFile(Path path) throws ControllerException {
            try {
                return new File(path.toString());
            } catch (Exception ex) {
                throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                        .withErrorMessage(ex.getMessage())
                        .withInternalErrorResponseMessage()
                        .build();
            }
        }

        public boolean isFileTooLarge(long fileSize) {
            return fileSize > this.maxByteSize;
        }

        public boolean isFileTypeAllowed(String fileType) {
            return this.allowedFileTypes.contains(fileType);
        }
    }

    public static class AWS {
        private final String accessKey;
        private final String secretKey;
        private final String region;
        private final String host;
        private final int port;
        private final String protocol;

        public AWS(String accessKey, String secretKey, String region, String host,
                   int port, String protocol) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
            this.region = region;
            this.host = host;
            this.port = port;
            this.protocol = protocol;
        }

        public String getEndpointURL() {
            Map<String, Object> urlValuesMap = Map.ofEntries(
                    Map.entry("protocol", this.protocol),
                    Map.entry("host", this.host),
                    Map.entry("portNumber", this.port)
            );

            String urlTemplate = "${protocol}://${host}:${portNumber}";
            StringSubstitutor sub = new StringSubstitutor(urlValuesMap);
            return sub.replace(urlTemplate);
        }

        public String getAccessKey() {
            return accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public String getRegion() {
            return region;
        }
    }
}
