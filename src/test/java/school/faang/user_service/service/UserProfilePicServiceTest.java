package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.FileStorageService;
import school.faang.user_service.util.ImageService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfilePicServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private UserProfilePicService userProfilePicService;

    @Test
    void upload_User_Not_Found() {
        MultipartFile file = new MockMultipartFile("Name", new byte[1]);

        DataValidException exception = assertThrows(DataValidException.class, () -> {
            userProfilePicService.upload(file, 1L);
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

        UserProfilePicDto result = userProfilePicService.upload(file, 1L);

        assertNotNull(result);
        assertEquals("BIG", result.getFileId());
        assertEquals("SMALL", result.getSmallFileId());
        assertEquals(1L, result.getUserId());
    }

}