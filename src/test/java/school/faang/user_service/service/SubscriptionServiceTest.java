package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;

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
    SubscriptionMapper INSTANCE;
    @Mock
    UserFilterDto userFilterDto;
    @Mock
    UserFilter userFilter;


    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2;
        followeeId = 1;
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
        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void testGetFollowers() {
        User user = mock(User.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(userStream);
        when(userFilter.filterUsers(userStream, userFilterDto)).thenReturn(List.of(user));

        subscriptionService.getFollowers(followeeId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(userFilter, times(1)).filterUsers(userStream, userFilterDto);
    }

    @Test
    public void testGetFollowing() {
        User user = mock(User.class);
        Stream<User> userStream = Stream.of(user);

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(userStream);
        when(userFilter.filterUsers(userStream, userFilterDto)).thenReturn(List.of(user));

        subscriptionService.getFollowing(followerId, userFilterDto);

        verify(subscriptionRepository, times(1)).findByFollowerId(followerId);
        verify(userFilter, times(1)).filterUsers(userStream, userFilterDto);
    }
}