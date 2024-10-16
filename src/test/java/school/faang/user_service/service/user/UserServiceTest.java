package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.filter.UsernameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.publisher.EventPublisher;
import school.faang.user_service.service.publisher.RedisTopics;
import school.faang.user_service.service.publisher.event.SearchAppearanceEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserFilterDto filterDto;
    private User user;
    private Premium premium;
    private List<Long> userIds;

    @Mock
    private UserFilter filterMock;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> filters;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
        user = new User();
        premium = new Premium();
        userIds = Arrays.asList(1L, 2L);
    }

    @Test
    void shouldReturnFilteredPremiumUsers_WhenFilterIsApplied() {
        premium.setUser(user);
        List<Premium> premiumList = List.of(premium);

        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        when(premiumRepository.findAll()).thenReturn(premiumList);
        when(userMapper.toDto(user)).thenReturn(userDto);

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

    @Test
    void shouldReturnUserDto_WhenUserFound() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserDto userDto = new UserDto(userId, "username", "email@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userId);

        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void shouldThrowUserNotFoundException_WhenUserNotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(0)).toDto(any());
    }

    @Test
    void shouldReturnListOfUserDtos_WhenUsersFound() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("email1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("email2@example.com");

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");
        user3.setEmail("email3@example.com");

        List<User> users = Arrays.asList(user1, user2, user3);

        UserDto userDto1 = new UserDto(1L, "user1", "email1@example.com");
        UserDto userDto2 = new UserDto(2L, "user2", "email2@example.com");
        UserDto userDto3 = new UserDto(3L, "user3", "email3@example.com");

        when(userRepository.findAllById(userIds)).thenReturn(users);
        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);
        when(userMapper.toDto(user3)).thenReturn(userDto3);

        List<UserDto> result = userService.getUsersByIds(userIds);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList(userDto1, userDto2, userDto3)));

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
        verify(userMapper, times(1)).toDto(user3);
    }

    @Test
    void shouldReturnEmptyList_WhenNoUsersFound() {
        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        List<UserDto> result = userService.getUsersByIds(userIds);

        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(0)).toDto(any());
    }

    @Test
    void getFilteredUsers_validRequest_returnsListOfUsers() {
        UserFilter mockUsernameFilter = mock(UsernameFilter.class);
        filters = new ArrayList<>(List.of(mockUsernameFilter));
        UserFilter usernameFilter = new UsernameFilter();
        filters.add(usernameFilter);
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("User1");
        User user1 = new User();
        user1.setUsername("User1");
        UserDto userDto1 = new UserDto(1L, "User1", "email");

        when(userRepository.findAll()).thenReturn(List.of(user1));
        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userContext.getUserId()).thenReturn(3L);

        List<UserDto> filteredUsers = userService.getFilteredUsers(userFilterDto);

        assertEquals(1, filteredUsers.size());
        ArgumentCaptor<SearchAppearanceEvent> eventCaptor = ArgumentCaptor.forClass(SearchAppearanceEvent.class);
        verify(eventPublisher, times(1)).publishToTopic(eq(RedisTopics.SEARCH_APPEARANCE.getTopicName()), eventCaptor.capture());

        for (SearchAppearanceEvent event : eventCaptor.getAllValues()) {
            assertEquals(3L, event.getActorId());
            assertNotNull(event.getReceivedAt());
            assertEquals(1L, event.getReceiverId());
        }
    }
}
