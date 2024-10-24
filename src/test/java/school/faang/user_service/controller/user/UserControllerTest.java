package school.faang.user_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.exception.handler.GlobalRestExceptionHandler;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static final long MAX_IMAGE_SIZE_MB = 5;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private UserDto userDto;
    private UserDto anotherUserDto;
    private List<Long> userIds;
    private UserFilterDto userFilterDto;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserController userController;


    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(2L);
        anotherUserDto = new UserDto();
        anotherUserDto.setId(3L);

        userFilterDto = new UserFilterDto();

        objectMapper = new ObjectMapper();

        userController.setMaxImageSizeMb(5);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalRestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return a certain user for a given existing ID")
    public void testGetUser_Success() throws Exception {
        long validId = 1L;

        when(userService.getUser(validId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", validId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService).getUser(validId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user is not found by ID")
    public void testGetUser_NonExistingId() throws Exception {
        long invalidId = 100500L;

        when(userService.getUser(invalidId)).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            throw new EntityNotFoundException("User not found with id: " + id);
        });

        mockMvc.perform(get("/users/{userId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(EntityNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage().contains("User not found with id: 100500")));

        verify(userService).getUser(invalidId);
    }

    @Test
    @DisplayName("Should return users for given list of IDs")
    public void testGetUsersByIds_Success() throws Exception {
        List<UserDto> dtos = Arrays.asList(userDto, anotherUserDto);
        userIds = Arrays.asList(2L, 3L);

        when(userService.getUsersByIds(userIds)).thenReturn(dtos);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        verify(userService).getUsersByIds(userIds);
    }

    @Test
    @DisplayName("Should return empty list when no users are found for given IDs")
    void testGetUsersByIds_NotFound() throws Exception {
        userIds = List.of(100L, 200L, 300L);

        when(userService.getUsersByIds(userIds)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getUsersByIds(userIds);
    }

    @Test
    @DisplayName("Should return filtered users successfully with correct user ID and filters")
    public void testGetFilteredUsers_Success() throws Exception {
        Long userId = 1L;
        String requestBody = objectMapper.writeValueAsString(userFilterDto);

        when(userContext.getUserId()).thenReturn(userId);
        when(userService.getFilteredUsers(userFilterDto, userId)).thenReturn(List.of(userDto, anotherUserDto));

        mockMvc.perform(post("/users/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid json is provided")
    public void testGetFilteredUsers_InvalidRequest() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/users/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Input Format"))
                .andExpect(jsonPath("$.message")
                        .value(containsString("JSON parse error")));
    }

    @Test
    void testSaveAvatar_FileTooLarge() throws Exception {
        long maxSizeBytes = MAX_IMAGE_SIZE_MB * 1024 * 1024;
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

    @Test
    public void testUpdateTelegramUserId_Success() {
        String telegramUserName = "testTelegramUserName";
        String telegramUserId = "123456789";

        doNothing().when(userService).updateTelegramUserId(telegramUserName, telegramUserId);

        userController.updateTelegramUserId(telegramUserName, telegramUserId);

        verify(userService, times(1)).updateTelegramUserId(telegramUserName, telegramUserId);
    }

    @Test
    @DisplayName("Should return user profile when valid user id and header provided")
    public void getUserProfile_Success() throws Exception {
        userDto.setUsername("Test User");

        when(userContext.getUserId()).thenReturn(108L);
        when(userService.getUser(2L)).thenReturn(userDto);

        mockMvc.perform(get("/users/2/profile")
                        .header("x-user-id", "108"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("Test User"));
    }
}
