package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.UserService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;

    @Test
    void testSaveAvatar_FileTooLarge() throws Exception {
        long maxSizeBytes = maxImageSizeMb * 1024 * 1024;
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[(int) (maxSizeBytes + 1)]);

        mockMvc.perform(multipart("/users/avatar")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("File size is too large!")));

        verify(userService, never()).saveAvatar(anyLong(), any(MultipartFile.class));
    }

    @Test
    void testSaveAvatar_InvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, new byte[1024]);

        mockMvc.perform(multipart("/users/avatar")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid file type. Only images jpg are allowed!")));

        verify(userService, never()).saveAvatar(anyLong(), any(MultipartFile.class));
    }

    @Test
    public void testGetAvatar_Success() throws Exception {
        byte[] avatarBytes = new byte[1024];
        when(userService.getAvatar(anyLong())).thenReturn(avatarBytes);

        mockMvc.perform(get("/users/avatar")
                        .header("x-user-id", 1L))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(avatarBytes));

        verify(userService, times(1)).getAvatar(anyLong());
    }


    @Test
    void testDeleteAvatar_Success() throws Exception {
        mockMvc.perform(delete("/users/avatar")
                        .header("x-user-id", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteAvatar(anyLong());
    }
}
