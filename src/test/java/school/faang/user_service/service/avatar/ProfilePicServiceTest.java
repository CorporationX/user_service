package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfilePicServiceTest {
    @InjectMocks
    private ProfilePicService profilePicService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AmazonS3 s3Client;
    @Spy
    private PictureMapper pictureMapper = Mappers.getMapper(PictureMapper.class);
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    @Value("${services.s3.smallSize}")
    private int smallSize;
    @Value("${services.s3.largeSize}")
    private int largeSize;
    @Captor
    private ArgumentCaptor<String> forLargePicture;
    @Captor
    private ArgumentCaptor<String> forSmallPicture;
    private User user;

    @BeforeEach
    public void init(){
        user = User.builder().id(1L).build();
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
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Test
    public void testSaveProfilePicWithSaving(){
        MockMultipartFile file = new MockMultipartFile("file", "example.jpg",
                MediaType.IMAGE_JPEG_VALUE, getImageBytes());
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        UserProfilePicDto result = profilePicService.saveProfilePic(user.getId(),file);

        verify(userRepository, times(1)).findById(user.getId());
        verify(s3Client, times(1)).putObject(bucketName, forSmallPicture.capture(), any(InputStream.class), null);
        verify(s3Client, times(1)).putObject(bucketName, forLargePicture.capture(), any(InputStream.class), null);
        verify(userRepository, times(1)).save(user);
        verify(pictureMapper, times(1)).toDto(UserProfilePic.builder()
                .smallFileId(forSmallPicture.getValue())
                .fileId(forLargePicture.getValue())
                .build());

        assertEquals(result.getFileId(), forLargePicture.getValue());
        assertEquals(result.getSmallFileId(), forSmallPicture.getValue());
    }
}
