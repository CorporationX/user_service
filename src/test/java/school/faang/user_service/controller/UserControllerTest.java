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
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
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
    private UserDto dto;

    @BeforeEach
    public void setUp() {
        dto = new UserDto();
        dto.setId(VALID_USER_ID);
        dto.setActive(false);
        dto.setGoalsIds(List.of(VALID_USER_ID));
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testUserDtoIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> controller.deactivatesUserProfile(INVALID_ID_FOR_USER)
        );
    }

    @Test
    public void testShouldDeactivateUserProfileAndReturnStatus200() throws Exception {
        Mockito.when(service.deactivatesUserProfile(VALID_USER_ID)).thenReturn(dto);

        mockMvc.perform(put("/api/user/{id}/deactivate", VALID_USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldDeactivateUserProfileWithCorrectID() throws Exception {
        Mockito.when(service.deactivatesUserProfile(VALID_USER_ID)).thenReturn(dto);

        mockMvc.perform(put("/api/user/{id}/deactivate", VALID_USER_ID))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    public void testShouldUpdateUserProfileStatusUponDeactivation() throws Exception {
        Mockito.when(service.deactivatesUserProfile(VALID_USER_ID)).thenReturn(dto);

        mockMvc.perform(put("/api/user/{id}/deactivate", VALID_USER_ID))
                .andExpect(jsonPath("$.id").value(VALID_USER_ID));
    }

    @Test
    public void testGetPremiumUsersWhenUserFilterDtoIsNotNull() {
        UserFilterDto userFilterDto = new UserFilterDto();
        List<UserDto> users = List.of(new UserDto());
        when(service.getPremiumUsers(userFilterDto)).thenReturn(users);

        List<UserDto> result = controller.getPremiumUsers(userFilterDto);
        assertThat(result).isNotNull();
    }

    @Test
    public void testGetEmptyListWhenNoSuchPremiumUsers() {
        UserFilterDto userFilterDto = new UserFilterDto();
        when(service.getPremiumUsers(userFilterDto)).thenReturn(Collections.emptyList());

        List<UserDto> result = controller.getPremiumUsers(userFilterDto);
        assertEquals(Collections.emptyList(), result);
    }
}