package school.faang.user_service.controller.user;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.service.user.UserAvatarService;

@ExtendWith(MockitoExtension.class)
public class UserAvatarControllerTest {

    private MockMvc mockMvc;

    @InjectMocks private UserAvatarController userAvatarController;

    @Mock private UserAvatarService userAvatarService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAvatarController).build();
    }

    @Test
    @DisplayName("Should successfully upload avatar when a valid file is provided")
    void uploadAvatar_shouldReturnSuccessMessage() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "test image".getBytes());

        doNothing().when(userAvatarService).uploadAvatar(anyLong(), any());

        mockMvc.perform(multipart("/api/v1/users/avatar/1").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar uploaded successfully"));

        verify(userAvatarService, times(1)).uploadAvatar(anyLong(), any());
    }

    @Test
    @DisplayName("Should return the large avatar image successfully")
    void downloadLargeAvatar_shouldReturnImage() throws Exception {
        when(userAvatarService.downloadLargeAvatar(1L))
                .thenReturn(
                        new InputStreamResource(new ByteArrayInputStream("test image".getBytes())));

        mockMvc.perform(get("/api/v1/users/avatar/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string("Content-Disposition", "inline"))
                .andExpect(content().bytes("test image".getBytes()));

        verify(userAvatarService, times(1)).downloadLargeAvatar(1L);
    }

    @Test
    @DisplayName("Should return the small avatar image successfully")
    void downloadSmallAvatar_shouldReturnImage() throws Exception {
        when(userAvatarService.downloadSmallAvatar(1L))
                .thenReturn(
                        new InputStreamResource(
                                new ByteArrayInputStream("test small image".getBytes())));

        mockMvc.perform(get("/api/v1/users/avatar/1/compressed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string("Content-Disposition", "inline"))
                .andExpect(content().bytes("test small image".getBytes()));

        verify(userAvatarService, times(1)).downloadSmallAvatar(1L);
    }

    @Test
    @DisplayName("Should successfully delete avatar when a valid user ID is provided")
    void deleteAvatar_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(userAvatarService).deleteAvatar(1L);

        mockMvc.perform(delete("/api/v1/users/avatar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar deleted successfully"));

        verify(userAvatarService, times(1)).deleteAvatar(1L);
    }

    @Test
    @DisplayName("Should return Bad Request when an empty file is uploaded")
    void uploadAvatar_shouldReturnBadRequest_whenFileIsEmpty() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "avatar.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/api/v1/users/avatar/1").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));

        verify(userAvatarService, never()).uploadAvatar(anyLong(), any());
    }
}
