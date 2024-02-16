package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void createUser_whenUserIsCorrect_thenRunService() {
        // Arrange
        UserCreateDto userDto = UserCreateDto.builder()
                .username("Elvis")
                .email("email@gmail.com")
                .password("password")
                .phone("12345")
                .countryId(4L).build();
        // Act
        userController.createUser(userDto);
        // Assert
        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void getUser() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        when(userService.getUser(userId)).thenReturn(userDto);
        userController.getUser(userId);
        verify(userService, times(1)).getUser(userId);
    }
}