package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserMapper userMapper = new UserMapperImpl();

    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        userService = new UserService(userRepository, userMapper);

        user = User.builder()
                .id(1L)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .build();
    }

    @DisplayName("Test successfully getting user by id")
    @Test
    public void testGetByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());

        verify(userRepository, times(1)).findById(1L);
    }

    @DisplayName("Test user not found by id")
    @Test
    public void testGetByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getById(1L);
        });

        assertEquals("User with 1 id doesn't exist", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
    }
}

