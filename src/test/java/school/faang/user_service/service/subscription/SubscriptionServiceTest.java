package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.ShortUserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ShortUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filters.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private ShortUserMapper shortUserMapper;
    @Mock
    private List<UserFilter> userFilters;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private long followerId;
    private long followeeId;

    @BeforeEach
    void setUp() {
        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    void testYourselfSubscription() {
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followerId);
    }

    @Test
    void testExistsSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followeeId);
    }

    @Test
    void testNotExistsSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testYourselfUnsubscription() {
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followerId));
        verify(subscriptionRepository, times(0)).unfollowUser(followerId, followerId);
    }

    @Test
    void testUnsubscription() {
        assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testGetFollowers() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(new User(), new User()));
        Mockito.when(shortUserMapper.toDto(any())).thenReturn(new ShortUserDto(1L, "", ""));

        List<ShortUserDto> followers = subscriptionService.getFollowers(followeeId, new UserFilterDto());

        assertEquals(2, followers.size());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(shortUserMapper, times(2)).toDto(any());
    }

    @Test
    void testGetFollowersCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(200);

        assertEquals(200, subscriptionService.getFollowersCount(followerId));
    }

    @Test
    void testGetFollowing() {
        Mockito.when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(new User(), new User()));
        Mockito.when(shortUserMapper.toDto(any())).thenReturn(new ShortUserDto(1L, "", ""));

        List<ShortUserDto> followers = subscriptionService.getFollowing(followerId, new UserFilterDto());

        assertEquals(2, followers.size());
        verify(subscriptionRepository, times(1)).findByFollowerId(followerId);
        verify(shortUserMapper, times(2)).toDto(any());
    }

    @Test
    void testGetFollowingCount() {
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(100);

        assertEquals(100, subscriptionService.getFollowingCount(followeeId));
    }

}
