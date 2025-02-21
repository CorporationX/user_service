package school.faang.user_service.controller;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.GlobalExceptionHandler;
import school.faang.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private final UserDto dto = new UserDto();

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(userController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();

        dto.setId(1L);
        dto.setUsername("John");
        dto.setEmail("john@gmail.com");

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test should return user by his ID")
    void getUser() throws Exception {
        Mockito.when(userService.getUser(1L)).thenReturn(dto);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("John"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));

        Mockito.verify(userService, Mockito.times(1)).getUser(1L);
    }

    @Test
    @DisplayName("Test should return users by their IDs")
    void getUsersByIds() throws Exception {
        Mockito.when(userService.getUsersByIds(List.of(1L))).thenReturn(List.of(dto));

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(List.of(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@gmail.com"));

        Mockito.verify(userService, times(1)).getUsersByIds(List.of(1L));
    }

    @Test
    @DisplayName(
            "Test should return DeactivatedUserDto when the ID of the non-deactivated user is transmitted")
    void testDeactivateUserSuccessful() throws Exception {
        String dateTimeString = "2025-02-01 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTimeFromString = LocalDateTime.parse(dateTimeString, formatter);

        long deactivatedUserId = 1L;

        DeactivatedUserDto deactivatedUserDto = new DeactivatedUserDto();
        deactivatedUserDto.setId(deactivatedUserId);
        deactivatedUserDto.setUsername("JohnDoe");
        deactivatedUserDto.setEmail("johndoe@example.com");
        deactivatedUserDto.setPhone("1234567890");
        deactivatedUserDto.setAboutMe("About John Doe");
        deactivatedUserDto.setActive(true);
        deactivatedUserDto.setCity("New York");
        deactivatedUserDto.setCountryId(1L);
        deactivatedUserDto.setExperience(2);
        deactivatedUserDto.setCreatedAt(localDateTimeFromString);
        deactivatedUserDto.setUpdatedAt(localDateTimeFromString);

        Mockito.when(userService.deactivateUser(deactivatedUserId)).thenReturn(deactivatedUserDto);

        mockMvc.perform(
                        post("/api/v1/users/{id}/deactivate", deactivatedUserId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deactivatedUserDto.getId()))
                .andExpect(jsonPath("$.username").value(deactivatedUserDto.getUsername()))
                .andExpect(jsonPath("$.email").value(deactivatedUserDto.getEmail()))
                .andExpect(jsonPath("$.phone").value(deactivatedUserDto.getPhone()))
                .andExpect(jsonPath("$.aboutMe").value(deactivatedUserDto.getAboutMe()))
                .andExpect(jsonPath("$.active").value(deactivatedUserDto.isActive()))
                .andExpect(jsonPath("$.city").value(deactivatedUserDto.getCity()))
                .andExpect(jsonPath("$.countryId").value(deactivatedUserDto.getCountryId()))
                .andExpect(jsonPath("$.experience").value(deactivatedUserDto.getExperience()))
                .andExpect(jsonPath("$.createdAt").value(dateTimeString))
                .andExpect(jsonPath("$.updatedAt").value(dateTimeString));

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(userService, Mockito.times(1)).deactivateUser(argumentCaptor.capture());

        Assertions.assertEquals(deactivatedUserId, argumentCaptor.getValue());
    }
}
