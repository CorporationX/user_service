package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);

        user = new User();
        user.setId(1L);
    }

    @Test
    public void testGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDto result = userService.getUserById(1L);

        verify(userRepository, times(1)).findById(1L);
        assertEquals(result, userDto);
    }

    @Test
    public void testGetUserByIdWithNoResult() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testBanUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.banUser("1");

        assertTrue(user.isBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testBanUserWithWrongUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.banUser("1");

        assertFalse(user.isBanned());
        verify(userRepository, times(0)).save(user);
    }
}
