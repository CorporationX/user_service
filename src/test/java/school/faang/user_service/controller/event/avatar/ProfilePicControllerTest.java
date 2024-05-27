package school.faang.user_service.controller.event.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class ProfilePicControllerTest {
    @MockBean
    private ProfilePicServiceImpl profilePicService;
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private final PictureMapper mapper = Mappers.getMapper(PictureMapper.class);
    @Value("${test.api.version}")
    private String versionApi;
    @Value("${server.port}")
    private String port;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).userProfilePic(new UserProfilePic("Big picture", "Small picture")).build();
    }

    @Test
    void testSaveProfilePic() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "example.jpg", MediaType.IMAGE_JPEG_VALUE, getImageBytes());
        when(profilePicService.saveProfilePic(user.getId(), file)).thenReturn(mapper.toDto(user.getUserProfilePic()));
        mockMvc.perform(multipart("http://localhost:"+port+ versionApi + "/pic/" + user.getId()).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileId").value(user.getUserProfilePic().getFileId()))
                .andExpect(jsonPath("$.smallFileId").value(user.getUserProfilePic().getSmallFileId()));

        verify(profilePicService, times(1)).saveProfilePic(user.getId(), file);
    }

    @Test
    void testGetProfilePic() throws Exception {
        InputStreamResource inputStream = new InputStreamResource(new ByteArrayInputStream(getImageBytes()));
        when(profilePicService.getProfilePic(user.getId())).thenReturn(inputStream);

        mockMvc.perform(get("http://localhost:"+port+versionApi + "/pic/" + user.getId())).andExpect(status().isOk()).andExpect(content().contentType("application/json"));

        verify(profilePicService, times(1)).getProfilePic(user.getId());
    }

    @Test
    void testDeleteProfilePic() throws Exception {
        when(profilePicService.deleteProfilePic(user.getId())).thenReturn("The user's avatar with the ID: " + user.getId() + " has been successfully deleted");

        mockMvc.perform(delete("http://localhost:"+port+versionApi + "/pic/" + user.getId())).andExpect(status().isOk()).andExpect(content().string("The user's avatar with the ID: " + user.getId() + " has been successfully deleted"));

        verify(profilePicService, times(1)).deleteProfilePic(user.getId());
    }
}
