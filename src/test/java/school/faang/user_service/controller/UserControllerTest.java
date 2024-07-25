package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final long INVALID_ID_FOR_USER = -3L;
    private static final long VALID_USER_ID = 1L;
    private MockMvc mockMvc;
    @Mock
    private UserService service;
    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setUp() {
        //Arrange
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testUserDtoIsNull() {
        //Assert
        assertThrows(
                RuntimeException.class,
                () -> controller.deactivatesUserProfile(INVALID_ID_FOR_USER)
        );
    }

    @Test
    public void testVerifyServiceDeactivatesUserProfile() throws Exception {
        //Arrange
        UserDto dto = new UserDto();
        dto.setId(VALID_USER_ID);
        dto.setActive(true);
        dto.setGoalsIds(List.of(VALID_USER_ID));
        dto.setOwnedEventsIds(List.of(VALID_USER_ID));
        //Act
        Mockito.when(service.deactivatesUserProfile(Mockito.anyLong())).thenReturn(dto);
        //Assert
        mockMvc.perform(put("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_USER_ID))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.goalsIds", hasSize(1)))
                .andExpect(jsonPath("$.ownedEventsIds", hasSize(1)));
    }

}