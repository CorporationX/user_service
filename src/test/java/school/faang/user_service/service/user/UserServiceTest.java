package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import school.faang.user_service.validator.user.UserValidator;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private UserService userService;

    @Test
    void test_GetUser_NotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void test_GetUser_ReturnsUser() {
        Long userId = 1L;
        User user = User.builder().id(1L).email("buk@mail.ru").username("buk").build();
        UserDto userExpected = userMapper.toDtoUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertEquals(userExpected, userService.getUser(userId));
        verify(userRepository).findById(userId);

    }

    @Test
    void test_getUsersByIds_ReturnsUsers() {
        List<Long> ids = List.of(1L, 2L);
        User firstUser = User.builder().id(1L).email("buk@mail.ru").username("buk").build();
        User secondUser = User.builder().id(2L).email("duk@mail.ru").username("buk").build();
        List<UserDto> usersExpected = userMapper.toDtoUser(List.of(firstUser, secondUser));

        when(userRepository.findAllById(ids)).thenReturn(List.of(firstUser, secondUser));

        assertEquals(usersExpected, userService.getUsersByIds(ids));
        verify(userRepository).findAllById(ids);
    }
    @Test
    void testCreateUserSuccess() {
        UserDto userDto = new UserDto();
        User user = new User();

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        assertDoesNotThrow(() -> userService.create(userDto));

        verify(userValidator).validatePassword(userDto);
        verify(userMapper).toEntity(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void testCreateUserFailure() {
        UserDto userDto = null;

        assertThrows(NullPointerException.class, () -> userService.create(userDto));

    }
}

