package school.faang.user_service.amazon_s3;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.FileException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {
    @InjectMocks
    private S3ServiceImpl s3ServiceImpl;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private MockMultipartFile anyMultipartFile;

    private Long anyLong;
    private Long anyUserId;
    private User anyUser;
    private String anyString;
    private String anyKey;
    private int firstRequiredMaxImageWidthAndLength;
    private String anyFolderName;
    private String anyBucketName;

    @BeforeEach
    public void setUp() {
        anyLong = 1L;
        anyUserId = anyLong;
        anyUser = new User();
        anyString = "anyString";
        anyKey = anyString;
        anyFolderName = "1anyString";
        anyBucketName = "amazonBucket";
        firstRequiredMaxImageWidthAndLength = 1080;
    }

    @Test
    public void uploadFileHasProblemWithAmazonWork() {
        assertThrows(FileException.class, () -> s3ServiceImpl.uploadFile(
                anyUserId,
                anyMultipartFile,
                anyFolderName,
                firstRequiredMaxImageWidthAndLength
                )
        );
    }

    @Test
    public void deleteFileSuccessfullyDeletes() {
        s3ServiceImpl.deleteFile(anyKey);

        verify(s3Client, times(1)).deleteObject(anyBucketName, anyKey);
    }

    @Test
    public void downloadFileHasProblemWithAmazonWork() {
        assertThrows(FileException.class, () -> s3ServiceImpl.downloadFile(anyKey));
    }
}
