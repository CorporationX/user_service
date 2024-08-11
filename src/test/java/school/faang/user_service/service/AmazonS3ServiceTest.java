package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.AmazonCredentials;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {
    private static final String BUCKET_NAME = "bucket";
    private static final String URL = "url";

    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private AmazonCredentials amazonCredentials;
    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @Test
    @DisplayName("Проверка вызова метода на сохранение фото")
    void testUploadPhoto() {
        when(amazonCredentials.getBucketName()).thenReturn(BUCKET_NAME);

        assertDoesNotThrow(() -> amazonS3Service.uploadFile(URL, new byte[1]));
        verify(amazonS3, times((1))).putObject(any());
    }
}
