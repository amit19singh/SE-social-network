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
import java.util.Objects;

@Service
public class StorageService {
    final private Storage storage;
    final private String bucketName;
    @Value("${app.max-file-size}")
    private long MAX_FILE_SIZE;

//    Autowiring
    public StorageService(Storage storage, @Value("${google.cloud.storage.bucket-name}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        // Validate file type and size
        if (!isValidFileType(Objects.requireNonNull(file.getContentType()))) {
            throw new IllegalArgumentException("Invalid file type.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit.");
        }
        String folderName = determineFolder(file.getContentType());
        String fullFileName = folderName + "/" + fileName;
        BlobId blobId = BlobId.of(bucketName, fullFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, file.getBytes());
        return String.format("File %s uploaded to bucket %s as %s", file.getOriginalFilename(), bucketName, fileName);
    }

    private String determineFolder(String contentType) {
        if (contentType.startsWith("image/")) {
            return "images";
        } else if (contentType.startsWith("video/")) {
            return "videos";
        } else {
            return "others";
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png")
                || contentType.equals("image/jpg") || contentType.equals("video/mp4");
    }

}


