package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.user_filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;
    @Mock
    SubscriptionRepository subscriptionRepository;
    @Spy
    UserMapper userMapper;
    @Mock
    UserFilterDto userFilterDto;
    @Mock
    List<UserFilter> userFilters;

    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2L;
        followeeId = 1L;
    }

    @Test
    public void testAssertThrowsDataValidationExceptionForMethodFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testMessageThrowForMethodFollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        try {
            subscriptionService.followUser(followerId, followeeId);
        } catch (DataValidationException e) {
            assertEquals("This subscription already exists", e.getMessage());
        }
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    public void testUnfollowUser() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true).thenReturn(false);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        User user = mock(User.class);
        UserDto userDto = mock(UserDto.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(userStream);
        when(userMapper.toDto(user)).thenReturn(userDto);

        subscriptionService.getFollowers(followeeId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testGetFollowing() {
        User user = mock(User.class);
        UserDto userDto = mock(UserDto.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(followerId)).thenReturn(userStream);
        when(userMapper.toDto(user)).thenReturn(userDto);

        subscriptionService.getFollowers(followerId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followerId);
        verify(userMapper, times(1)).toDto(user);
    }
}