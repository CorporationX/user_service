package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserFilterDto filterDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("test@mail.com")
                .phone("1234567890")
                .active(true)
                .experience(5)
                .build();
    }

    @Test
    void getPremiumUsers_WhenSuccess() {
        List<UserDto> premiumUsers = List.of(userDto);
        when(userService.getPremiumUsers(filterDto)).thenReturn(premiumUsers);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }

    @Test
    void getPremiumUsers_WhenEmptyList() {
        List<UserDto> premiumUsers = List.of();
        when(userService.getPremiumUsers(filterDto)).thenReturn(premiumUsers);

        List<UserDto> result = userController.getPremiumUsers(filterDto);

        assertEquals(0, result.size());
        verify(userService, times(1)).getPremiumUsers(filterDto);
    }
}