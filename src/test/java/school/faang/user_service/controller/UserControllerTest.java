package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.TestDataFactory;

import java.util.List;

import static java.util.List.of;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUser() throws Exception {
        // given - precondition
        var userDto = TestDataFactory.createUserDto();
        var userId = userDto.getId();

        when(userService.findUserById(userId))
                .thenReturn(userDto);

        // when - action
        var response = mockMvc.perform(get("/api/users/{userId}", userId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUsersByIds() throws Exception {
        // given - precondition
        List<Long> userIds = of(1L, 2L, 3L);
        var userDtoList = TestDataFactory.createUserDtosList();

        when(userService.findUsersByIds(userIds))
                .thenReturn(userDtoList);
        // when - action
        var response = mockMvc.perform(post("/api/users/")
                .content(new ObjectMapper().writeValueAsString(userIds))
                .contentType("application/json")
        );

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(userDtoList.size())))
                .andExpect(jsonPath("$[0].id").value(userDtoList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(userDtoList.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(userDtoList.get(2).getId()));
    }
}