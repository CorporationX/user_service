package school.faang.user_service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnPremiumUsers_WhenFilterIsApplied() {
        // Given
        UserFilterDto filterDto = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        List<UserDto> userDtos = List.of(userDto);

        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        // When
        List<UserDto> result = userController.getPremiumUsers(filterDto);

        // Then
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }

    @Test
    void shouldReturnEmptyList_WhenNoPremiumUsers() {
        // Given
        UserFilterDto filterDto = new UserFilterDto();
        List<UserDto> userDtos = List.of();

        when(userService.getPremiumUsers(filterDto)).thenReturn(userDtos);

        // When
        List<UserDto> result = userController.getPremiumUsers(filterDto);

        // Then
        assertTrue(result.isEmpty());
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }
}
