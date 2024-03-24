package org.sn.socialnetwork.service;


import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.sn.socialnetwork.service.StorageService;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {
    @Mock
    private Storage mockedStorage;

    @Value("${app.max-file-size}")
    private long MAX_FILE_SIZE;

    @InjectMocks
    private StorageService storageService;



    @Test
    public void testUploadFileInvalidFileType() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test-file.txt", "application/pdf", "PDF content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storageService.uploadFile(mockFile, "test-file"));
        assertEquals("Invalid file type.", exception.getMessage());
        verifyNoInteractions(mockedStorage);
    }
}
