package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserFilter userFilter;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserFilterDto filterDto;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        userDto1 = UserDto.builder().id(1L).username("user1").build();
        userDto2 = UserDto.builder().id(2L).username("user2").build();

        filterDto = new UserFilterDto();

        userService = new UserServiceImpl(userRepository, userMapper, List.of(userFilter));
    }

    @Test
    void getPremiumUsers_WhenNoUsersFound_ReturnsEmptyList() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.empty());

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(0, result.size());
        verify(userRepository, times(1)).findPremiumUsers();
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void getPremiumUsers_WithUsers_ReturnsUserDto() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(2, result.size());
        assertEquals(userDto1, result.get(0));
        assertEquals(userDto2, result.get(1));
        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
    }

    @Test
    void getPremiumUsers_WithFiltersApplied_ReturnsFilteredUserDto() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2));
        when(userFilter.isApplicable(filterDto)).thenReturn(true);
        when(userFilter.apply(user1, filterDto)).thenReturn(true);
        when(userFilter.apply(user2, filterDto)).thenReturn(false);
        when(userMapper.toDto(user1)).thenReturn(userDto1);

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto1, result.get(0));
        verify(userRepository, times(1)).findPremiumUsers();
        verify(userFilter, times(1)).apply(user1, filterDto);
        verify(userFilter, times(1)).apply(user2, filterDto);
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, never()).toDto(user2);
    }
}
