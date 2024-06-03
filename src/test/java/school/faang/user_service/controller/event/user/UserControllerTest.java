package school.faang.user_service.controller.event.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder().username("name").email("test@mail.ru").password("password").build();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        String json = "{\n\"username\": \"" + userDto.getUsername()+"\",\n" +
                "\"email\": \""+userDto.getEmail()+ "\",\n" +
                "\"password\": \""+userDto.getPassword() + "\"\n}";

        mockMvc.perform(post( "/users/creature")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.password").value(userDto.getPassword()));

        verify(userService, times(1)).createUser(userDto);
    }
}
