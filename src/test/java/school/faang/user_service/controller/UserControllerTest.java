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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private UserDto userDto;
    private UserDto anotherUserDto;
    private UserFilterDto userFilterDto;

    @Mock
    private UserService userService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        anotherUserDto = new UserDto();
        anotherUserDto.setId(2L);

        userFilterDto = new UserFilterDto();

        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("Should return filtered users successfully with correct user ID and filter")
    public void testGetFilteredUsers_Success() throws Exception {
        Long userId = 1L;
        String requestBody = objectMapper.writeValueAsString(userFilterDto);

        when(userContext.getUserId()).thenReturn(userId);
        when(userService.getFilteredUsers(userFilterDto, userId)).thenReturn(List.of(userDto, anotherUserDto));

        mockMvc.perform(get("/users/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid json is provided")
    public void testGetFilteredUsers_InvalidRequest() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(get("/users/filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
