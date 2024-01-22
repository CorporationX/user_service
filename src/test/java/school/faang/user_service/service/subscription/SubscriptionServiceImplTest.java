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

    private static final long VALID_FOLLOWER_ID = 5L;
    private static final long VALID_FOLLOWEE_ID = 2L;
    private static final long INVALID_FOLLOWER_ID = -1L;
    private static final long INVALID_FOLLOWEE_ID = -2L;
    private static final long SAME_USER_ID = 1L;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void shouldDeleteSubscriptionWhenFollowerAndFolloweeIdsAreDifferent() {
        subscriptionService.unfollowUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);

        verify(subscriptionRepository).unfollowUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);
    }

    @Test
    void shouldCreateSubscriptionWhenNotExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID))
                .thenReturn(Boolean.FALSE);

        subscriptionService.followUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);

        verify(subscriptionRepository).followUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID);
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(VALID_FOLLOWER_ID, VALID_FOLLOWEE_ID));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(INVALID_FOLLOWER_ID, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(1L, INVALID_FOLLOWEE_ID));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreTheSame() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(SAME_USER_ID, SAME_USER_ID));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(INVALID_FOLLOWER_ID, 1L));
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(1L, INVALID_FOLLOWEE_ID));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreTheSame() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(SAME_USER_ID, SAME_USER_ID));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

}