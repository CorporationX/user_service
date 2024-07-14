package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.User_Service;
import school.faang.user_service.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private User_Service user_service;

    @Mock
    private UserRepository user_repository;

    @Spy
    private UserMapperImpl user_mapper = new UserMapperImpl();

    @Spy
    private UserValidator user_validator = new UserValidator(user_repository);

    @InjectMocks
    private UserController userController;

    private Long userId = 1L;
    private User user = User.builder().id(userId).build();
    private UserDto userDto = user_mapper.toUserDto(user);

    @Test
    public void testDeactivateUserThrowsException() {
        Assertions.assertThrows(UserValidationException.class, () -> userController.deactivateUser(0L));
    }

    @Test
    public void testDeactivateUser() {
        Mockito.doNothing().when(user_validator).validateUserId(userId);
        Mockito.doNothing().when(user_service).removeMenteeAndGoals(userId);
        Mockito.when(user_service.deactivate(userId)).thenReturn(userDto);
        userController.deactivateUser(userId);
        Mockito.verify(user_service, Mockito.times(1)).deactivate(userId);
        Mockito.verify(user_service, Mockito.times(1)).removeMenteeAndGoals(userId);
    }

}
