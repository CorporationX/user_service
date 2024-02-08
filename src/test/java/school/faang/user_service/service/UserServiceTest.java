package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl();

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_whenUserExists_thenReturnUserDto() {
        long userId = 1L;
        User user = new User();
        user.setUsername("Bob");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals("Bob", result.getUsername());
    }

    @Test
    void getUser_whenUserNotExists_getUserThrowsException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> userService.getUser(userId));

        assertEquals("Пользователя не существует", dataValidationException.getMessage());
    }

    void getUserById_whenUserIdExist_thenReturnUser() {
        // Arrange
        long userId = 1;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.getUserById(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        assertEquals(user.getId(), userId);
    }

    @Test
    void testGetUserById_whenUserIdNotExist_thenThrowEntityNotFoundException() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }
}