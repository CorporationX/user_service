package school.faang.user_service.controller;

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
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private ObjectMapper objectMapper;

    private long userId;
    private MockMvc mockMvc;
    private String userDtoJson;
    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userId = 1L;

        UserDto userDto = UserDto.builder()
                .username("username")
                .password("password")
                .country(1L)
                .email("test@mail.com")
                .phone("123456")
                .build();


        mockMultipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Mock file content".getBytes()
        );

        ObjectMapper objectMapperImpl = new ObjectMapper();
        userDtoJson = objectMapperImpl.writeValueAsString(userDto);
    }

    @Test
    @DisplayName("testing createUser method")
    public void testCreateUser() throws Exception {
        mockMvc.perform(multipart("/users/new")
                        .file("file", mockMultipartFile.getBytes())
                        .file("userJson", userDtoJson.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
        verify(objectMapper, times(1)).readValue(userDtoJson, UserDto.class);
    }

    @Test
    @DisplayName("testing createUser method")
    public void testUpdateUserAvatar() throws Exception {
        MockMultipartHttpServletRequestBuilder builder = multipart("/users/{userId}/avatar", userId);
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
    public void testDeactivateUserWithUserServiceExecution(){
        long userId = 1L;
        userController.deactivateUser(userId);
        verify(userService, times(1)).deactivateUser(userId);
    }
}