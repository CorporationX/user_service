package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> filters;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFilteredPremiumUsers_WhenFilterIsApplied() {
        UserFilterDto filterDto = new UserFilterDto();
        User user = new User();
        Premium premium = new Premium();
        premium.setUser(user);
        List<Premium> premiumList = List.of(premium);

        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        when(premiumRepository.findAll()).thenReturn(premiumList);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserFilter filterMock = mock(UserFilter.class);
        when(filterMock.apply(any(UserDto.class), any(UserFilterDto.class))).thenReturn(true);
        when(filters.stream()).thenReturn(Stream.of(filterMock));

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(premiumRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(user);
        verify(filterMock, times(1)).apply(userDto, filterDto);
    }

    @Test
    void shouldReturnEmptyListWhenNoPremiumUsers() {
        UserFilterDto filterDto = new UserFilterDto();
        when(premiumRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(0, result.size());
        verify(premiumRepository, times(1)).findAll();
    }

    @Test
    void shouldApplyFiltersToPremiumUsers_WhenFilterIsApplied() {
        UserFilterDto filterDto = new UserFilterDto();
        User user = new User();
        Premium premium = new Premium();
        premium.setUser(user);
        List<Premium> premiumList = List.of(premium);

        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        when(premiumRepository.findAll()).thenReturn(premiumList);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserFilter filterMock = mock(UserFilter.class);
        when(filterMock.apply(userDto, filterDto)).thenReturn(true);
        when(filters.stream()).thenReturn(Stream.of(filterMock));

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(filterMock, times(1)).apply(userDto, filterDto);
    }
}
