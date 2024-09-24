package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldReturnPremiumUsers_WhenFilterIsApplied() {
        UserFilterDto filterDto = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        List<UserDto> userDtos = List.of(userDto);

        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }

    @Test
    void shouldReturnEmptyList_WhenNoPremiumUsers() {
        UserFilterDto filterDto = new UserFilterDto();
        List<UserDto> userDtos = List.of();

        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertTrue(result.isEmpty());
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }
}
