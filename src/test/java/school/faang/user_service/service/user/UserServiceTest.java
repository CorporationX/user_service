package school.faang.user_service.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .city("Test City")
                .experience(1)
                .premium(null)
                .build();

        userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .city(user.getCity())
                .experience(user.getExperience())
                .premium(user.getPremium())
                .build();
    }

    @Test
    public void testGetPremiumUsers_IsRunFindPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .city("Rostov")
                .experience(500)
                .build();

        userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
    }

    @Test
    public void testGetUser() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testGetUser_NotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.getUser(userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        User user2 = User.builder().id(2L).username("testUser2").email("test2@example.com").build();
        UserDto userDto2 = UserDto.builder().id(2L).username("testUser2").email("test2@example.com").build();

        when(userRepository.findAllById(ids)).thenReturn(List.of(user, user2));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userDto, result.get(0));
        assertEquals(userDto2, result.get(1));

        verify(userRepository, times(1)).findAllById(ids);
        verify(userMapper, times(1)).toDto(user);
        verify(userMapper, times(1)).toDto(user2);
    }
}
