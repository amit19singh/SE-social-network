package org.sn.socialnetwork.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class StorageService {
    final private Storage storage;
    final private String bucketName;

//    Autowiring
    public StorageService(Storage storage, @Value("${google.cloud.storage.bucket-name}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, file.getBytes());
        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, fileName);
    }

}
