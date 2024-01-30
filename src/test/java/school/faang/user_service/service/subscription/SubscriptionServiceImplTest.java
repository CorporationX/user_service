package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void shouldDeleteSubscriptionWhenFollowerAndFolloweeIdsAreDifferent() {
        long followerId = 5L;
        long followeeId = 2L;

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void shouldCreateSubscriptionWhenNotExists() {
        long followerId = 5L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(Boolean.FALSE);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionExists() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreInvalid() {
        long invalidFollowerId = -1L;
        long invalidFolloweeId = -2L;

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(invalidFollowerId, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(1L, invalidFolloweeId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreTheSame() {
        long sameUserId = 1L;

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreInvalid() {
        long invalidFollowerId = -1L;
        long invalidFolloweeId = -2L;

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(invalidFollowerId, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(1L, invalidFolloweeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreTheSame() {
        long sameUserId = 1L;

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

}