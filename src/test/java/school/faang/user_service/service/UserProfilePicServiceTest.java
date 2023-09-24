package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.AvatarFromAwsDto;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.publisher.ProfilePicPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.FileStorageService;
import school.faang.user_service.util.ImageService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfilePicServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ImageService imageService;
    @Mock
    private ProfilePicPublisher profilePicPublisher;
    @InjectMocks
    private UserProfilePicService userProfilePicService;

    @Test
    void upload_User_Not_Found() {
        MultipartFile file = new MockMultipartFile("Name", new byte[1]);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userProfilePicService.uploadWithPublishProfilePicEvent(file, 1L);
        });

        assertEquals("User with id 1 is not found", exception.getMessage());
    }

    @Test
    void uploadTest() {
        byte[] byteFile = {1, 2, 3, 4, 5};
        MultipartFile file = new MockMultipartFile("Name", byteFile);
        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(imageService.resizeImage(file, true)).thenReturn(byteFile);
        when(imageService.resizeImage(file, false)).thenReturn(byteFile);
        when(fileStorageService.uploadFile(byteFile, file, 1L, "big")).thenReturn("BIG");
        when(fileStorageService.uploadFile(byteFile, file, 1L, "small")).thenReturn("SMALL");

        UserProfilePicDto result = userProfilePicService.uploadWithPublishProfilePicEvent(file, 1L);

        assertNotNull(result);
        assertEquals("BIG", result.getFileId());
        assertEquals("SMALL", result.getSmallFileId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void userProfilePic_Null() {
        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        DataValidException exception = assertThrows(DataValidException.class, () -> {
            userProfilePicService.getByUserId(1L);
        });

        assertEquals("User with id 1 doesn't has an avatar", exception.getMessage());
    }

    @Test
    void getByUserTest() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("1");
        userProfilePic.setSmallFileId("2");

        User user = new User();
        user.setId(1L);
        user.setUserProfilePic(userProfilePic);

        byte[] array = {1};

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(fileStorageService.receiveFile(anyString())).thenReturn(new AvatarFromAwsDto(array, "jpg"))
                .thenReturn(new AvatarFromAwsDto(array, "png"));

        List<AvatarFromAwsDto> result = userProfilePicService.getByUserId(1L);

        assertNotNull(result);
        assertEquals("jpg", result.get(0).getContentType());
        assertEquals("png", result.get(1).getContentType());
        assertEquals(array, result.get(0).getFile());
        assertEquals(array, result.get(1).getFile());
    }

    @Test
    void deleteByUserIdTest() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("1");
        userProfilePic.setSmallFileId("2");

        User user = new User();
        user.setId(1L);
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userProfilePicService.deleteByUserId(1L);

       assertNull(user.getUserProfilePic());
    }
}