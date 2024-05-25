package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfilePicServiceImplTest {
    @InjectMocks
    private ProfilePicServiceImpl profilePicService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AmazonS3 s3Client;
    @Spy
    private PictureMapper pictureMapper = Mappers.getMapper(PictureMapper.class);
    @Captor
    private ArgumentCaptor<String> forPictures;
    private User user;
    private String backetName = "user-bucket";

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .userProfilePic(new UserProfilePic("Big picture", "Small picture"))
                .build();
    }

    private byte[] getImageBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fill(new Rectangle2D.Double(0, 0, 50, 50));
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
    public void testSaveProfilePicWithSaving() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        MockMultipartFile file = new MockMultipartFile("file", "example.jpg", MediaType.IMAGE_JPEG_VALUE, getImageBytes());
        ReflectionTestUtils.setField(profilePicService, "smallSize", 170);
        ReflectionTestUtils.setField(profilePicService, "largeSize", 1080);
        ReflectionTestUtils.setField(profilePicService, "bucketName", backetName);

        UserProfilePicDto result = profilePicService.saveProfilePic(user.getId(), file);

        verify(userRepository, times(1)).findById(user.getId());
        verify(s3Client, times(2)).putObject(eq(backetName), forPictures.capture(), any(InputStream.class), any());
        var pictures = forPictures.getAllValues();
        String forSmallPicture = pictures.get(0);
        String forLargePicture = pictures.get(1);
        verify(userRepository, times(1)).save(user);
        verify(pictureMapper, times(1)).toDto(UserProfilePic.builder().smallFileId(forSmallPicture).fileId(forLargePicture).build());
        assertEquals(result.getFileId(), forLargePicture);
        assertEquals(result.getSmallFileId(), forSmallPicture);
    }

    @Test
    public void testSaveProfilePicWithEntityNotFoundException(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                ()->profilePicService.saveProfilePic(user.getId(), null));
        assertEquals(exception.getMessage(), "User with id: " + user.getId() + " was not found");
    }

    @Test
    public void testGetProfilePic() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        ReflectionTestUtils.setField(profilePicService, "bucketName", backetName);
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream(getImageBytes()));
        when(s3Client.getObject(eq(backetName), any())).thenReturn(s3Object);

        InputStreamResource result = profilePicService.getProfilePic(user.getId());
        verify(s3Client, times(1)).getObject(backetName, user.getUserProfilePic().getFileId());
        assertNotNull(result);
    }

    @Test
    public void testGetProfilePicWithEntityNotFoundException(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                ()->profilePicService.getProfilePic(user.getId()));
        assertEquals(exception.getMessage(), "User with id: " + user.getId() + " was not found");
    }

    @Test
    public void testDeleteProfilePic() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        ReflectionTestUtils.setField(profilePicService, "bucketName", backetName);

        String result = profilePicService.deleteProfilePic(user.getId());
        verify(s3Client, times(2)).deleteObject(eq(backetName), anyString());
        verify(userRepository, times(1)).save(user);
        assertNull(user.getUserProfilePic().getFileId());
        assertNull(user.getUserProfilePic().getSmallFileId());
        assertEquals(result, "The user's avatar with the ID: " + user.getId() + " has been successfully deleted");
    }

    @Test
    public void testDeleteProfilePicWithEntityNotFoundException(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                ()->profilePicService.deleteProfilePic(user.getId()));
        assertEquals(exception.getMessage(), "User with id: " + user.getId() + " was not found");
    }
}
