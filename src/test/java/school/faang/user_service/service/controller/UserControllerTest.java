package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.user.UserValidator;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private UserValidator userValidator = new UserValidator(userRepository);

    @InjectMocks
    private UserController userController;

    private Long userId = 1L;
    private User user = User.builder().id(userId).build();
    private UserDto userDto = userMapper.toDto(user);

    @Test
    public void testDeactivateUserThrowsException() {
        Assertions.assertThrows(UserValidationException.class, () -> userController.deactivateUser(0L));
    }

    @Test
    public void testDeactivateUser() {
        Mockito.doNothing().when(userValidator).validateUserId(userId);
        Mockito.doNothing().when(userService).removeMenteeAndGoals(userId);
        Mockito.when(userService.deactivate(userId)).thenReturn(userDto);
        userController.deactivateUser(userId);
        Mockito.verify(userService, Mockito.times(1)).deactivate(userId);
        Mockito.verify(userService, Mockito.times(1)).removeMenteeAndGoals(userId);
    }

}
