package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserCreateDto;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.MessageError.GENERATION_EXCEPTION;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserProfilePicMapper userProfilePicMapper = Mappers.getMapper(UserProfilePicMapper.class);
    @Mock
    private RestTemplate restTemplate;
    @Mock
    MultipartFile multipartFile;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private UserProfilePicService userProfilePicService;

    private String bucketName;
    private User user;

    @BeforeEach
    void setUp() {
        bucketName = "test_bucket";
        multipartFile = Mockito.mock(MultipartFile.class);
        user = User.builder()
                .id(1L)
                .username("testName")
                .email("test@example.com")
                .password("****")
                .userProfilePic(new UserProfilePic("big", "small"))
                .build();


        ReflectionTestUtils.setField(userProfilePicService, "smallSize", 170);
        ReflectionTestUtils.setField(userProfilePicService, "largeSize", 1080);
        ReflectionTestUtils.setField(userProfilePicService, "bucketName", "test_bucket");
        ReflectionTestUtils.setField(userProfilePicService, "url", "http://example.com/default_avatar.jpg");
    }

    private byte[] getImageBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fill(new Rectangle2D.Double(0, 0, 100, 100));
        byte[] bytes;
        try {
            ImageIO.write(image, "jpg", outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    @Test
    void testPutDefaultPicWhileCreating_success() throws IOException {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setUsername("testName");
        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        when(userContext.getUserId()).thenReturn(user.getId());
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(imageBytes);

        assertDoesNotThrow(() -> userProfilePicService.putDefaultPicWhileCreating());

        assertNotNull(user.getUserProfilePic());
        assertNotNull(user.getUserProfilePic().getFileId());
        verify(s3Client, times(2)).putObject(eq(bucketName), anyString(), any(InputStream.class), isNull());
    }

    @Test
    void testPutDefaultPicWhileCreating_nullImage() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setUsername("testName");

        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(null);
        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> userProfilePicService.putDefaultPicWhileCreating());

        assertEquals(GENERATION_EXCEPTION.getMessage(), ex.getMessage());
        verify(s3Client, never()).putObject(eq(bucketName), anyString(), any(InputStream.class), isNull());
    }

    @Test
    void testSaveUserProfilePic_success() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", getImageBytes());
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userService.findUserById(user.getId())).thenReturn(user);
        UserProfilePicDto res = userProfilePicService.saveUserProfilePic(multipartFile);

        ArgumentCaptor<String> named = ArgumentCaptor.forClass(String.class);

        verify(s3Client, times(2)).putObject(eq(bucketName), named.capture(), any(InputStream.class), isNull());
        var pictures = named.getAllValues();
        String forSmallPicture = pictures.get(0);
        String forLargePicture = pictures.get(1);
        verify(userRepository).save(user);
        verify(userProfilePicMapper, times(1)).toDto(UserProfilePic.builder().smallFileId(forSmallPicture).fileId(forLargePicture).build());
        assertEquals(res.getFileId(), forLargePicture);
        assertEquals(res.getSmallFileId(), forSmallPicture);
    }

    @Test
    void testGetProfilePic() {
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream(getImageBytes()));

        when(userService.findUserById(user.getId())).thenReturn(user);
        when(s3Client.getObject(bucketName, user.getUserProfilePic().getFileId())).thenReturn(s3Object);

        InputStream res = userProfilePicService.getUserProfilePic(user.getId());

        verify(s3Client).getObject(bucketName, user.getUserProfilePic().getFileId());
        assertNotNull(res);
    }

    @Test
    void testDeleteProfilePic() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userService.findUserById(user.getId())).thenReturn(user);

        userProfilePicService.deleteUserProfilePic();

        verify(s3Client, times(2)).deleteObject(eq(bucketName), anyString());
        assertNull(user.getUserProfilePic());
    }
}
