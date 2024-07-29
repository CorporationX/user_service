package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final long INVALID_ID_FOR_USER = -3L;
    private static final long VALID_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void testVerifyServiceDeactivatesUserProfile() throws Exception {
        //Arrange
        UserDto dto = new UserDto();
        dto.setId(VALID_USER_ID);
        dto.setActive(true);
        dto.setGoalsIds(List.of(VALID_USER_ID));
        dto.setOwnedEventsIds(List.of(VALID_USER_ID));
        //Act
        Mockito.when(service.deactivatesUserProfile(anyLong())).thenReturn(dto);
        //Assert
        mockMvc.perform(put("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.goalsIds", hasSize(1)))
                .andExpect(jsonPath("$.ownedEventsIds", hasSize(1)));
    }

    @Test
    public void testGetUser() throws Exception {
        UserDto firstUser = new UserDto();
        firstUser.setId(VALID_USER_ID);
        firstUser.setUsername("Sasha");
        firstUser.setEmail("sasha@yandex.ru");
        Mockito.when(service.getUser(anyLong())).thenReturn(firstUser);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(VALID_USER_ID)))
                .andExpect(jsonPath("$.userName", is("Sasha")))
                .andExpect(jsonPath("$.email", is("sasha@yandex.ru")));
    }
}