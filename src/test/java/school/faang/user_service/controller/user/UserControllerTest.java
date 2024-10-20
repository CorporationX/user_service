package school.faang.user_service.controller.user;

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
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user1 = UserDto.builder()
                .id(1L)
                .email("someone@gmail.com")
                .username("someone")
                .build();

        user2 = UserDto.builder()
                .id(2L)
                .email("somebody@gmail.com")

                .username("somebody")
                .build();
    }

    @Test
    void testGetPremiumUsers() {
        userController.getPremiumUsers(any());
        verify(userService, times(1)).getPremiumUsers(any());
    }

    @Test
    void testDeactivateUserProfile() {
        userController.deactivateUserProfile(1L);

        verify(userService).deactivateUserProfile(1L);
    }

    @Test
    @DisplayName("Get User")
    void testGetUser() throws Exception {
        doReturn(user1).when(userService).getUser(anyLong());

        mockMvc.perform(get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("someone@gmail.com"))
                .andExpect(jsonPath("$.username").value("someone"));

    }

    @Test
    @DisplayName("Get Users By Ids")
    void testGetUsersByIds() throws Exception {
        doReturn(List.of(user1, user2)).when(userService).getUsersByIds(anyList());

        mockMvc.perform(get("/api/v1/users/")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("user_id", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].email").value("someone@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("somebody@gmail.com"))
                .andExpect(jsonPath("$[0].username").value("someone"))
                .andExpect(jsonPath("$[1].username").value("somebody"));
    }
}
