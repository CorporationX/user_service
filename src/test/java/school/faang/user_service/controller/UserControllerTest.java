package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserContactsDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsResponseDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {UserController.class})
class UserControllerTest {
    private static final Long USER_ID = 1L;
    private static final Long CURRENT_USER_ID = 2L;
    private static final PreferredContact PREFERENCE = PreferredContact.EMAIL;
    private static final String CHANNEL_NAME = "recommendation_request_channel";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @MockBean
    private UserValidator userValidator;

    private String csvContent;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).
                setControllerAdvice(new GlobalExceptionHandler())
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        String testCsv = IOUtils.toString(ClassLoader.getSystemClassLoader()
                .getSystemResourceAsStream("students2.csv"));
        file = new MockMultipartFile("file", "test.csv", "text/csv", testCsv.getBytes());
    }

    @Test
    void getUserWhenUserExistsShouldReturnUser() throws Exception {
        long userId = 1L;
        UserDto dto = new UserDto();
        dto.setId(userId);

        when(userService.findUserDtoById(userId)).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findUserDtoById(userId);
    }

    @Test
    void testGetAllUsers_NoFilter() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("JaneSmith")
                .build();

        when(userService.getAllUsers(any(UserFilterDto.class))).thenReturn(Arrays.asList(userDto1, userDto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[1].username").value("JaneSmith"));

        verify(userService).getAllUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_NoFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("PremiumUser")
                .build();

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/premium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("PremiumUser"));

        verify(userService).getPremiumUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetAllUsers_WithFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        when(userService.getAllUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .param("namePattern", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"));

        verify(userService).getAllUsers(any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_WithFilter() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("PremiumJohn")
                .build();

        when(userService.getPremiumUsers(any(UserFilterDto.class))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/premium")
                        .param("namePattern", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("PremiumJohn"));

        verify(userService).getPremiumUsers(any(UserFilterDto.class));
    }

    @Test
    void testUploadToCsvSuccess() throws Exception {
        ProcessResultDto mockResult = new ProcessResultDto(1, List.of());
        when(userService.importUsersFromCsv(any(InputStream.class))).thenReturn(mockResult);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countSuccessfullySavedUsers").value(1))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void getUsersByIdsShouldReturnUserDtosWhenUsersExist() throws Exception {
        List<UserDto> userDtos = Arrays.asList(
                UserDto.builder().id(1L).username("John Doe").build(),
                UserDto.builder().id(2L).username("Jane Doe").build()
        );

        when(userService.getUsersByIds(Arrays.asList(1L, 2L))).thenReturn(userDtos);

        mockMvc.perform(get("/users/ids")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Jane Doe"));

        verify(userService, times(1)).getUsersByIds(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUsersByIdsShouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userService.getUsersByIds(Arrays.asList(3L, 4L))).thenReturn(List.of());

        mockMvc.perform(get("/users/ids")
                        .param("ids", "3", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getUsersByIds(Arrays.asList(3L, 4L));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Update user's preferred contact method successfully")
    void testUpdateUserPreference_Success() throws Exception {
        UserContactsDto updatedUserContactsDto = UserContactsDto.builder()
                .id(USER_ID)
                .preference(PREFERENCE)
                .build();

        when(userService.updateUserPreferredContact(eq(USER_ID), eq(PREFERENCE), eq(CURRENT_USER_ID))).thenReturn(updatedUserContactsDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/contact-preference", USER_ID)
                        .param("preference", PREFERENCE.name())
                        .header("Current-User-Id", String.valueOf(CURRENT_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.preference").value(PREFERENCE.name()));

        verify(userService, times(1)).updateUserPreferredContact(eq(USER_ID), eq(PREFERENCE), eq(CURRENT_USER_ID));
        verifyNoMoreInteractions(userValidator, userService);
    }

    @Test
    @DisplayName("Update user's preferred contact method - Unauthorized")
    void testUpdateUserPreference_Unauthorized() throws Exception {
        doThrow(new SecurityException("Access denied"))
                .when(userService)
                .updateUserPreferredContact(eq(USER_ID), eq(PREFERENCE), eq(CURRENT_USER_ID));

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/contact-preference", USER_ID)
                        .param("preference", PREFERENCE.name())
                        .header("Current-User-Id", String.valueOf(CURRENT_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).updateUserPreferredContact(eq(USER_ID), eq(PREFERENCE), eq(CURRENT_USER_ID));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void saveProfileSettingsShouldReturnOk() throws Exception {
        Long userId = 1L;
        UserProfileSettingsDto settingsDto = UserProfileSettingsDto.builder().preference(PreferredContact.EMAIL).build();
        UserProfileSettingsResponseDto responseDto = new UserProfileSettingsResponseDto(1L, PreferredContact.EMAIL, userId);

        when(userService.saveProfileSettings(eq(userId), any(UserProfileSettingsDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users/1/profile-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "preference": "EMAIL"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.preference").value(responseDto.getPreference().toString()))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void getProfileSettingsShouldReturnOk() throws Exception {
        Long userId = 1L;
        UserProfileSettingsResponseDto responseDto = new UserProfileSettingsResponseDto(1L, PreferredContact.EMAIL, userId);

        when(userService.getProfileSettings(userId)).thenReturn(responseDto);

        mockMvc.perform(get("/users/1/profile-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.preference").value(responseDto.getPreference().toString()))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @DisplayName("Get user contacts success")
    void testGetUserContactsSuccess() throws Exception {
        Long userId = 1L;
        UserContactsDto dto = UserContactsDto.builder()
                .id(1L)
                .email("email")
                .phone("phone")
                .build();
        when(userService.getUserContacts(userId)).thenReturn(dto);
        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email"))
                .andExpect(jsonPath("$.phone").value("phone"));
    }

    @Test
    @DisplayName("Get user contacts fail: Negative project id")
    void testGetUserContacts_NegativeProjectId_Fail() throws Exception {
        Long userId = -1L;

        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User id should be a positive integer")));
    }

    @Test
    @DisplayName("Get user contacts fail: null project id")
    void testGetUserContacts_nullProjectId_Fail() throws Exception {
        Long userId = null;

        mockMvc.perform(get("/users/{userId}/contacts", userId))
                .andExpect(status().isNotFound());
    }
}
