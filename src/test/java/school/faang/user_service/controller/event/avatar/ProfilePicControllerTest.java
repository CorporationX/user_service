package school.faang.user_service.controller.event.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.ProfilePic.ProfilePicController;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.mapper.avatar.PictureMapperImpl;
import school.faang.user_service.service.avatar.ProfilePicServiceImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(value = "classpath:src/main/resources/application.yaml")
//@PropertySource("classpath:application.properties")
public class ProfilePicControllerTest {

    @InjectMocks
    private ProfilePicController controller;
    @Mock
    private ProfilePicServiceImpl profilePicService;
    private MockMvc mockMvc;
    private User user;
    private PictureMapper mapper = Mappers.getMapper(PictureMapper.class);
    private String versionApi = "/api/v1";

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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        user = User.builder().id(1L).userProfilePic(new UserProfilePic("Big picture", "Small picture")).build();
    }

    @Test
    void testSaveProfilePic() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "example.jpg", MediaType.IMAGE_JPEG_VALUE, getImageBytes());
        when(profilePicService.saveProfilePic(user.getId(), file)).thenReturn(mapper.toDto(user.getUserProfilePic()));
        ReflectionTestUtils.setField(profilePicService, "largeSize", 1080);
        mockMvc.perform(
                        post(versionApi +"/"+ user.getId())
                                .content(file.getBytes())
                                .contentType(file.getContentType()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileId").value(user.getUserProfilePic().getFileId()))
                .andExpect(jsonPath("$.smallFileId").value(user.getUserProfilePic().getSmallFileId()));

        verify(profilePicService, times(1)).saveProfilePic(user.getId(), file);
    }
}
