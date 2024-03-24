package com.davidruffner.inventorytrackercontroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class FunctionalTestConfig {
    private static final String LOCAL_PROPS_FILE_PATH = "src/it/resources/local-props.env";

    private String stripEnclosingQuotes(String originalStr) {
        int endCharIndex = originalStr.length() - 1;

        if (originalStr.charAt(0) == '\'' && originalStr.charAt(endCharIndex) == '\'') {
            return originalStr.substring(1, endCharIndex);
        } else {
            return originalStr;
        }
    }

    public FunctionalTestConfig(@Value("${secrets-source:localFile}") String secretsSource) {
        if (secretsSource.equals("localFile")) {
            try (Stream<String> lines = Files.lines(Paths.get(LOCAL_PROPS_FILE_PATH))) {
                lines.forEach(line -> {
                    if (null != line && !line.isEmpty()) {
                        String[] parts = line.split("=");
                        if (parts.length >= 2) {
                            String envVal = stripEnclosingQuotes(parts[1]);
                            System.setProperty(parts[0], envVal);
                        }
                    }
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
