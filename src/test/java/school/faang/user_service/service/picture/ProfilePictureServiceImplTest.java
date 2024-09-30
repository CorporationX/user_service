package school.faang.user_service.service.picture;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.AmazonS3Service;
import school.faang.user_service.service.GeneratorPictureService;
import school.faang.user_service.service.HelperAmazonS3Service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProfilePictureServiceImplTest {

    @Mock
    private AmazonS3Service s3Service;

    @Mock
    private HelperAmazonS3Service helperS3Service;

    @Mock
    private GeneratorPictureService generatorPictureService;

    @InjectMocks
    private ProfilePictureServiceImpl service;

    private final String username = "Robert";
    private final String mail = "robert@java.com";
    private final String firstKey = "first";
    private final String secondKey = "second";

    private final byte[] smallPicture = {4, 5, 6};
    private final byte[] picture = {1, 2, 3};
    private final List<byte[]> images = List.of(picture, smallPicture);

    private final UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
            .username(username)
            .email(mail)
            .build();
    private final UserProfilePic userProfilePic = UserProfilePic.builder()
            .fileId(firstKey)
            .smallFileId(secondKey)
            .build();

    private final ObjectMetadata firstMetadata = new ObjectMetadata();
    private final ObjectMetadata secondMetadata = new ObjectMetadata();

    @BeforeEach
    void setUp() {
        when(generatorPictureService.getProfilePictures(username)).thenReturn(images);
        when(helperS3Service.getKey(picture, username, mail)).thenReturn(firstKey);
        when(helperS3Service.getKey(smallPicture, username, mail)).thenReturn(secondKey);
        when(helperS3Service.getMetadata(picture)).thenReturn(firstMetadata);
        when(helperS3Service.getMetadata(smallPicture)).thenReturn(secondMetadata);
    }

    @Test
    void saveProfilePictures() {
        UserProfilePic result = service.saveProfilePictures(userRegistrationDto);

        assertEquals(userProfilePic, result);
        verifyFull();
    }

    private void verifyFull() {
        verify(generatorPictureService).getProfilePictures(username);
        verify(helperS3Service, times(2)).getKey(any(byte[].class), eq(username), eq(mail));
        verify(helperS3Service, times(2)).getMetadata(any(byte[].class));
        verify(s3Service, times(2))
                .uploadFile(any(byte[].class), any(ObjectMetadata.class), anyString());
    }
}