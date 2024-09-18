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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.handler.GlobalRestExceptionHandler;
import school.faang.user_service.service.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private UserDto userDto;
    private UserDto anotherUserDto;
    private List<Long> userIds;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(2L);
        anotherUserDto = new UserDto();
        anotherUserDto.setId(3L);

        objectMapper = new ObjectMapper();

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
}
