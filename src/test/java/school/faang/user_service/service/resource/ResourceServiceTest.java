package school.faang.user_service.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    AmazonS3 amazonClient;
    @InjectMocks
    ResourceService resourceService;

    private MultipartFile avatar;
    private S3Object s3Object;

    @BeforeEach
    void setUp() {
        avatar = mock(MultipartFile.class);
        s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("file.jpeg/byteInformation".getBytes()));
    }

    @Test
    void uploadFile_FileUploadedToMinio_ThenReturnedAsEntity() {
        when(avatar.getSize()).thenReturn(2097152L);
        when(avatar.getContentType()).thenReturn("image/jpeg");
        try {
            when(avatar.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        } catch (IOException e) {
            System.out.println("Why do i need to do that in test wtf");
        }
        String folderName = "test_folder";
        String expectedKey = String.format("%s/%s/%s",
                folderName, avatar.getOriginalFilename(), avatar.getContentType());

        Resource returned = resourceService.uploadFile(avatar, folderName);

        assertAll(
                () -> verify(amazonClient, times(1)).putObject(any(PutObjectRequest.class)),
                () -> assertDoesNotThrow(() -> resourceService.uploadFile(avatar, folderName)),
                () -> assertEquals(avatar.getOriginalFilename(), returned.getName()),
                () -> assertEquals(avatar.getSize(), returned.getSize().longValue()),
                () -> assertEquals(expectedKey, returned.getKey().substring(0, 27))
        );
    }

    @Test
    void getFile_FileExistsInMinio_ThenReturnedAsInputStream() {
        String key = "test_key/file.jpeg";
        when(amazonClient.getObject(null, key)).thenReturn(s3Object);

        InputStream returned = resourceService.getFile(key);

        assertAll(
                () -> verify(amazonClient, times(1)).getObject(null, key),
                () -> assertEquals(s3Object.getObjectContent(), returned),
                () -> assertDoesNotThrow(() -> resourceService.getFile(key))
        );
    }

    @Test
    void deleteFile_FileExistsInMinio_ThenDeleted() {
        String key = "test_key/file.jpeg";
        resourceService.deleteFile(key);

        verify(amazonClient, times(1)).deleteObject(null, key);
    }
}
