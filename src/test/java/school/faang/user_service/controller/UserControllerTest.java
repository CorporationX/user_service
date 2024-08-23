package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private long userId;
    private String userDtoJson;
    private MockMultipartFile mockMultipartFile;

    private String followerIdsJson;
    private List<Long> followersIds;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        userId = 1L;
        followersIds = List.of(1L, 2L);
        followerIdsJson = objectMapper.writeValueAsString(followersIds);

        String username = "username";
        String password = "password";
        long country = 1L;
        String email = "email";
        String phone = "123456";

        UserDto userDto = UserDto.builder()
                .username(username)
                .password(password)
                .country(country)
                .email(email)
                .phone(phone)
                .build();

        mockMultipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Mock file content".getBytes()
        );

        userDtoJson = objectMapper.writeValueAsString(userDto);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("testing getUser method")
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/v1/user/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    @DisplayName("testing checkUserExistence method")
    void testCheckUserExistence() throws Exception {
        mockMvc.perform(get("/api/v1/user/exists/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).checkUserExistence(userId);
    }

    @Test
    @DisplayName("testing getUserFollowers method")
    void testGetUserFollowers() throws Exception {
        mockMvc.perform(get("/api/v1/user/{userId}/followers", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserFollowers(userId);
    }

    @Test
    @DisplayName("testing doesFollowersExist method")
    void testDoesFollowersExist() throws Exception {
        mockMvc.perform(post("/api/v1/user/exists/followers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(followerIdsJson))
                .andExpect(status().isOk());
        verify(userService, times(1)).checkAllFollowersExist(followersIds);
    }


    @Test
    @DisplayName("testing createUser method")
    public void testCreateUser() throws Exception {
        mockMvc.perform(multipart("/api/v1/user")
                        .file("file", mockMultipartFile.getBytes())
                        .file("userJson", userDtoJson.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
        verify(objectMapper, times(1)).readValue(userDtoJson, UserDto.class);
    }

    @Test
    @DisplayName("testing createUser method")
    public void testUpdateUserAvatar() throws Exception {
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/v1/user/{userId}/avatar", userId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isAccepted());
        verify(userService, times(1)).updateUserAvatar(userId, mockMultipartFile);
    }

    @Test
    @DisplayName("testing deactivateUser userService deactivate deactivateUser method execution")
    public void testDeactivateUserWithUserServiceExecution() {
        long userId = 1L;
        userController.deactivateUser(userId);
        verify(userService, times(1)).deactivateUser(userId);
    }
}