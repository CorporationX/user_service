package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private FileService fileService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AvatarService avatarService;

    private User user;
    private byte[] avatarBytes;
    private String mockFileName;
    private File mockFile;
    private String mockSmallFileName;
    private File mockSmallFile;
    private ResponseEntity<Resource> responseEntity;

    @BeforeEach
    void setUp() {
        int seedRange = 100;

        avatarService.setSTYLES(new String[]{"style"});
        avatarService.setSEED_RANGE(seedRange);
        avatarService.setAVATAR_GENERATOR_URL_PATTERN("url");
        avatarService.setAVATAR_PREFIX("avatar");
        avatarService.setSMALL_AVATAR_PREFIX("small_avatar");
        avatarService.setFILE_EXTENSION("jpeg");
        avatarService.setSMALL_FILE_WIDTH(200);
        avatarService.setSMALL_FILE_HEIGHT(200);

        mockFileName = "avatar_1.jpeg";
        mockSmallFileName = "small_avatar_1.jpeg";

        user = User.builder()
                .id(1L)
                .build();

        avatarBytes = new byte[0];

        mockFile = mock(File.class);
        mockSmallFile = mock(File.class);

        Resource resource = new ByteArrayResource(avatarBytes);
        System.out.println(new Object().hashCode());
        responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Test
    @DisplayName("testing updateAvatarToRandom method")
    void testUpdateAvatarToRandom() {
        when(fileService.convertResponseToByteArray(responseEntity)).thenReturn(avatarBytes);
        when(fileService.resizeImageFile(mockFile, 200, 200, mockSmallFileName))
                .thenReturn(mockSmallFile);
        when(restTemplate.getForEntity(anyString(), eq(Resource.class))).thenReturn(responseEntity);
        when(fileService.convertByteArrayToFile(any(), eq(mockFileName))).thenReturn(mockFile);
        when(userRepository.save(any(User.class))).thenReturn(user);

        avatarService.setRandomAvatar(user);

        verify(fileService, times(1)).convertResponseToByteArray(responseEntity);
        verify(fileService, times(1)).resizeImageFile(mockFile, 200, 200, mockSmallFileName);
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(mockFile, times(1)).delete();
        verify(mockSmallFile, times(1)).delete();
        verify(userRepository, times(1)).save(any(User.class));
    }
}