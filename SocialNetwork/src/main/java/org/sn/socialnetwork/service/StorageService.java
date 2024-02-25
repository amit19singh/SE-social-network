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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public String uploadFile(MultipartFile file, String fileName, String fileType) throws IOException {
        if (!isValidFileType(Objects.requireNonNull(file.getContentType()))) {
            throw new IllegalArgumentException("Invalid file type.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit.");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fullFileName = fileName + "_" + timestamp;
        BlobId blobId = BlobId.of(bucketName, fileType + fullFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, file.getBytes());
        return String.format(fullFileName);
    }

    private boolean isValidFileType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png")
                || contentType.equals("image/jpg") || contentType.equals("video/mp4");
    }

}


