package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private UserFilterDto filterDto;
    private UserDto userDto;
    private List<UserDto> userDtos;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
        userDto = new UserDto(1L, "username", "email@example.com");
        userDtos = List.of(userDto);
    }

    @Test
    void shouldReturnPremiumUsers_WhenFilterIsApplied() {
        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }

    @Test
    void shouldReturnEmptyList_WhenNoPremiumUsers() {
        userDtos = Collections.emptyList();
        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertTrue(result.isEmpty());
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }
}
