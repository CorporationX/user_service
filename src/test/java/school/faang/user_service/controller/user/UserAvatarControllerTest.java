package school.faang.user_service.controller.user;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import school.faang.user_service.exception.FileSizeException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.service.user.UserAvatarService;

@ExtendWith(MockitoExtension.class)
class UserAvatarControllerTest {

    private MockMvc mockMvc;

    @Mock private UserAvatarService avatarService;

    @Mock private AmazonS3 s3Client;

    @InjectMocks private UserAvatarController avatarController;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(avatarController)
                        .setControllerAdvice(new ExceptionHandlerExceptionResolver())
                        .build();
    }

    @Test
    @DisplayName("Test uploading avatar successfully via controller")
    void uploadAvatar_success() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "avatar.jpg", "image/jpeg", "image content".getBytes());
        long userId = 1L;

        mockMvc.perform(multipart("/api/v1/users/{userId}/avatar", userId).file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar uploaded successfully"));

        verify(avatarService, times(1)).uploadAvatar(userId, file);
    }

    @Test
    @DisplayName("Test uploading avatar with file size exceeding the limit")
    void uploadAvatar_fileTooLarge() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "avatar.jpg", "image/jpeg", new byte[1024 * 1024 * 10]);
        long userId = 1L;

        doThrow(new FileSizeException("File size exceeds limit"))
                .when(avatarService)
                .uploadAvatar(userId, file);

        mockMvc.perform(multipart("/api/v1/users/{userId}/avatar", userId).file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error uploading avatar: File size exceeds limit"));

        verify(avatarService, times(1)).uploadAvatar(userId, file);
    }

    @Test
    @DisplayName("Test downloading large avatar successfully")
    void downloadLargeAvatar_success() throws Exception {
        long userId = 1L;
        byte[] imageBytes = "image content".getBytes();
        when(avatarService.downloadLargeAvatar(userId)).thenReturn(imageBytes);

        mockMvc.perform(get("/api/v1/users/{userId}/avatar/large", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));

        verify(avatarService, times(1)).downloadLargeAvatar(userId);
    }

    @Test
    @DisplayName("Test downloading small avatar when user is not found")
    void downloadSmallAvatar_notFound() throws Exception {
        long userId = 1L;
        when(avatarService.downloadSmallAvatar(userId))
                .thenThrow(new UserNotFoundException("Small avatar not found for user " + userId));

        mockMvc.perform(get("/api/v1/users/{userId}/avatar/small", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Small avatar not found for user " + userId));

        verify(avatarService, times(1)).downloadSmallAvatar(userId);
    }

    @Test
    @DisplayName("Test deleting avatar successfully")
    void deleteAvatar_success() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{userId}/avatar", userId))
                .andExpect(status().isNoContent());

        verify(avatarService, times(1)).deleteAvatar(userId);
    }

    @Test
    @DisplayName("Test deleting avatar when user is not found")
    void deleteAvatar_notFound() throws Exception {
        long userId = 1L;
        doThrow(new UserNotFoundException("Avatar not found for user " + userId))
                .when(avatarService)
                .deleteAvatar(userId);

        mockMvc.perform(delete("/api/v1/users/{userId}/avatar", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Avatar not found for user " + userId));

        verify(avatarService, times(1)).deleteAvatar(userId);
    }
}
