package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private UserController userController;

    @Test
    void createUser_whenUserIsCorrect_thenRunService() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("Elvis")
                .email("email")
                .password("password")
                .phone("12345")
                .countryId(4L).build();
        // Act
        userController.createUser(userDto);
        // Assert
        verify(userService, times(1)).createUser(userDto);
        verify(userValidator, times(1)).validateCreateUser(userDto);
    }
}