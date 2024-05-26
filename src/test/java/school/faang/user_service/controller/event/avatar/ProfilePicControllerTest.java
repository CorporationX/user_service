package school.faang.user_service.controller.event.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.avatar.ProfilePicController;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.service.avatar.ProfilePicServiceImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProfilePicControllerTest {

    @InjectMocks
    private ProfilePicController controller;
    @Mock
    private ProfilePicServiceImpl profilePicService;
    private MockMvc mockMvc;
    private User user;
    private final PictureMapper mapper = Mappers.getMapper(PictureMapper.class);
    private final String versionApi = "/api/v1";

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
        MockMultipartFile file = new MockMultipartFile("file",
                "example.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                getImageBytes());
        when(profilePicService.saveProfilePic(user.getId(), file)).thenReturn(mapper.toDto(user.getUserProfilePic()));
        ReflectionTestUtils.setField(controller, "maxSizeBytes", 5242880);
        mockMvc.perform(multipart(versionApi + "/pic/" + user.getId()).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileId")
                        .value(user.getUserProfilePic().getFileId()))
                .andExpect(jsonPath("$.smallFileId")
                        .value(user.getUserProfilePic().getSmallFileId()));

        verify(profilePicService, times(1)).saveProfilePic(user.getId(), file);
    }

    @Test
    void testGetProfilePic() throws Exception {
        InputStreamResource inputStream= new InputStreamResource(new ByteArrayInputStream(getImageBytes()));
        when(profilePicService.getProfilePic(user.getId())).thenReturn(inputStream);

        mockMvc.perform(get(versionApi + "/pic/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        verify(profilePicService, times(1)).getProfilePic(user.getId());
    }

    @Test
    void testDeleteProfilePic() throws Exception {
        when(profilePicService.deleteProfilePic(user.getId()))
                .thenReturn("The user's avatar with the ID: " + user.getId() + " has been successfully deleted");

        mockMvc.perform(delete(versionApi + "/pic/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("The user's avatar with the ID: " + user.getId() + " has been successfully deleted"));

        verify(profilePicService, times(1)).deleteProfilePic(user.getId());
    }


}
