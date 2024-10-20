/*
package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.service.user.UserService;

import javax.xml.bind.ValidationException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(UserController.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerWebMvcTest {

    private static final long USER_ID = 1L;

    private static final String EMAIL = "EMAIL";
    private static final String PHONE = "PHONE";
    private static final String USER_NAME = "USERNAME";

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapperImpl userMapper;

    @Autowired
    private UserHeaderFilter userHeaderFilter;

    @Autowired
    private UserContext userContext;

    @MockBean
    private UserService userService;


    private User user;
    private UserDto userDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(USER_ID)
                .username(USER_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();
    }

    @Test
    void withUser() throws Exception {
        when(userService.getUser(USER_ID))
                .thenReturn(userMapper.toDto(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.username").value(USER_NAME))
                .andExpect(jsonPath("$.phone").value(PHONE));

        verify(userService).getUserById(USER_ID);
    }

    @Test
    void withIncorrectUser() throws Exception {
        when(userService.getUser(USER_ID))
                .thenReturn(userMapper.toDto(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.email").value("TEST"))
                .andExpect(jsonPath("$.username").value("USER_NAME"))
                .andExpect(jsonPath("$.phone").value(PHONE));

        verify(userService).getUserById(USER_ID);
    }

    @Test
    void noUser() throws Exception {
        when(userService.getUser(USER_ID))
                .thenThrow(new ValidationException("User with id " + USER_ID + " does not exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{userId}", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id " + USER_ID + " does not exist"));
    }
}*/
