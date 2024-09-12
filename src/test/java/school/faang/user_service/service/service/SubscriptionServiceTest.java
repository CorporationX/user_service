package school.faang.user_service.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class SubscriptionServiceTest {
    private long followerId;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        followerId = 1L;
    }

    @Test
    void shouldFollowUserSuccessfully() throws Exception, DataValidationException {
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionAlreadyExists() {
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, never()).followUser(followerId, followeeId);
    }

    @Test
    void shouldUnfollowUserSuccessfully() {
        long followeeId = 2L;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testGetFollowers() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.userToUserDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void testGetFollowersWithFilter() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");
        UserDto userDto = new UserDto(1L, "username", "email@example.com");

        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.userToUserDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void testFilterUsers() throws Exception {
        Method method = SubscriptionService.class.getDeclaredMethod("filterUsers", List.class, UserFilterDto.class);
        method.setAccessible(true);

        UserDto user1 = new UserDto(1L, "user1", "email1@example.com");
        UserDto user2 = new UserDto(2L, "user2", "email2@example.com");
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("user1");

        List<UserDto> users = Arrays.asList(user1, user2);

        @SuppressWarnings("unchecked")
        List<UserDto> result = (List<UserDto>) method.invoke(subscriptionService, users, filter);

        assertEquals(1, result.size());
        assertEquals(user1, result.get(0));
    }

    @Test
    public void testGetFollowersCount() {
        long followeeId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId))
                .thenReturn(expectedCount);

        int actualCount = subscriptionService.getFollowersCount(followeeId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository, times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void testGetFollowing() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        filter.setNamePattern("username");

        UserDto userDto = new UserDto(1L, "username", "email@example.com");
        User user = User.builder()
                .id(1L)
                .username("username")
                .email("email@example.com")
                .build();

        when(subscriptionRepository.findByFollowerId(followeeId))
                .thenReturn(Stream.of(user));
        when(userMapper.userToUserDto(user))
                .thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowing(followeeId, filter);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

        verify(subscriptionRepository, times(1)).findByFollowerId(followeeId);
        verify(userMapper, times(1)).userToUserDto(user);
    }
    @Test
    void testGetFollowingCount() {
        int expectedCount = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(expectedCount);

        int result = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedCount, result);

        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }
}
