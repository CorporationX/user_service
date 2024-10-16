package school.faang.user_service.service.avatar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarFetchException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.minio.MinioService;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
    private static final Long USER_ID = 1L;
    private static final String LARGE_FILE_NAME = "large.jpg";
    private static final String SMALL_FILE_NAME = "small.jpg";
    private static final int LARGE_AVATAR_DIMENSION_CONSTRAINT = 800;
    private static final int SMALL_AVATAR_DIMENSION_CONSTRAINT = 300;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private MinioService minioService;
    @InjectMocks
    private AvatarService avatarService;
    private AvatarStyle style;
    private byte[] avatarData;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MultipartFile avatarFile;
    private byte[] mockImageData;

    @BeforeEach
    public void setup() {
        style = AvatarStyle.AVATAAARS;
        avatarData = new byte[]{1, 2, 3};
    }

    @Test
    public void testGenerateAndSaveAvatar_Success() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(avatarData, HttpStatus.OK));
        doNothing().when(minioService).uploadFile(anyString(), any(byte[].class), anyString());

        UserProfilePic userProfilePic = avatarService.generateAndSaveAvatar(style);

        assertNotNull(userProfilePic);
        assertNotNull(userProfilePic.getFileId());
        assertNotNull(userProfilePic.getSmallFileId());

        verify(minioService, times(2)).
                uploadFile(anyString(), eq(avatarData), eq("image/png"));
        verify(restTemplate, times(2)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    public void testGetRandomAvatar_Success() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(avatarData, HttpStatus.OK));

        byte[] result = avatarService.getRandomAvatar(style, "png", 200);

        assertNotNull(result);
        assertEquals(avatarData.length, result.length);

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    public void testGetRandomAvatar_Failure() {
        when(restTemplate.getForEntity(anyString(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(AvatarFetchException.class, () -> {
            avatarService.getRandomAvatar(style, "png", 200);
        });

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(byte[].class));
    }

    @Test
    void uploadUserAvatar_Success() throws Exception {
        BufferedImage bImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", os);
        byte[] content = os.toByteArray();
        MockMultipartFile mockFile = new MockMultipartFile("userfile", "originalFilename.png", "image/png", content);
        doNothing().when(userRepository).changeProfilePic(anyLong(), anyString(), anyString());

        UserProfilePic result = avatarService.uploadUserAvatar(1L, mockFile);

        assertNotNull(result);
        verify(userRepository).changeProfilePic(1L, result.getFileId(), result.getSmallFileId());
    }


    @Test
    void uploadUserAvatar_TooLarge() {
        when(avatarFile.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> avatarService.uploadUserAvatar(USER_ID, avatarFile));

        assertEquals("Cannot upload file bigger than 5Mb", exception.getMessage());
    }

    @Test
    void getUserAvatar_Success() {
        String mockFileId = "user_avatar.jpg";
        when(userRepository.findProfilePic(USER_ID)).thenReturn(mockFileId);
        byte[] mockImageData = new byte[10];
        when(minioService.downloadFile(mockFileId)).thenReturn(mockImageData);

        byte[] result = avatarService.getUserAvatar(USER_ID);

        Assertions.assertArrayEquals(mockImageData, result);
        verify(minioService).downloadFile(mockFileId);
    }

    @Test
    void deleteUserAvatar_Success() {
        String userProfilePicId = "avatar_large.jpg";
        String userProfilePicIdSmall = "avatar_small.jpg";

        when(userRepository.findProfilePic(USER_ID)).thenReturn(userProfilePicId);
        when(userRepository.findSmallProfilePic(USER_ID)).thenReturn(userProfilePicIdSmall);

        avatarService.deleteUserAvatar(USER_ID);

        verify(minioService).deleteFile(userProfilePicId);
        verify(minioService).deleteFile(userProfilePicIdSmall);
        verify(userRepository).deleteProfilePic(USER_ID);
    }
}
