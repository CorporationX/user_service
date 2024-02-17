package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.user.UserProfilePicController;
import school.faang.user_service.service.user.UserProfilePicService;

import java.io.File;
import java.io.InputStream;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserProfilePicControllerTest {

    @Mock
    private UserProfilePicService userProfilePicService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserProfilePicController userProfilePicController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        userContext.setUserId(0L);
        mockMvc = MockMvcBuilders.standaloneSetup(userProfilePicController).build();
    }

    @Test
    public void testUploadProfilePic() {
        MockMultipartFile mockFile = Mockito.mock(MockMultipartFile.class);
        try {
            mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/user/upload-profile-pic", 0L, mockFile)
                    .file("file", mockFile.getBytes()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("Users profile picture uploaded successfully"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
