package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.validator.UserControllerValidator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserControllerValidator validator;

    @InjectMocks
    private UserController userController;

    MockMvc mockMvc;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userDto = UserDto.builder().id(1L).build();
    }

    @Test
    public void testGetUser() throws Exception {
        Mockito.doNothing().when(validator).validateId(userDto.id());
        Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.id()))
                .andExpect(status().isOk());
        verify(validator).validateId(userDto.id());
    }

    @Test
    public void testGetUsersByIds() throws Exception {
        List<UserDto> userDtos = Arrays.asList(userDto, userDto);
        Mockito.when(userService.getUsersByIds(Mockito.anyList())).thenReturn(userDtos);

        mockMvc.perform(post("/users/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeactivateUser() throws Exception {
        long userId = 1;
        Mockito.when(userService.deactivateUser(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(post("/users/deactivate/{userId}", userId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.id()));

        verify(userService).deactivateUser(userId);
    }
}
