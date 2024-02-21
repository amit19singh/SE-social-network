package org.sn.socialnetwork.security_and_config;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    @Value("${google.cloud.storage.credentials-file-path}")
    private String credentialsFilePath;

    @Value("${google.cloud.storage.bucket-name}")
    private String bucketName;

    @Bean
    public Storage storage() throws IOException {
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFilePath));
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}

