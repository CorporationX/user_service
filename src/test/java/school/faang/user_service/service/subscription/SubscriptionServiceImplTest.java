package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.follower.FollowerEventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    private long validFollowerId;
    private long validFolloweeId;
    private long invalidFollowerId;
    private long invalidFolloweeId;
    private long sameUserId;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private FollowerEventPublisher followerEventPublisher;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void init() {
        validFollowerId = 5L;
        validFolloweeId = 2L;
        invalidFollowerId = -1L;
        invalidFolloweeId = -2L;
        sameUserId = 1L;
    }

    @Test
    void shouldDeleteSubscriptionWhenFollowerAndFolloweeIdsAreDifferent() {
        subscriptionService.unfollowUser(validFollowerId, validFolloweeId);

        verify(subscriptionRepository).unfollowUser(validFollowerId, validFolloweeId);
    }

    @Test
    void shouldCreateSubscriptionWhenNotExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(validFollowerId, validFolloweeId))
                .thenReturn(Boolean.FALSE);

        subscriptionService.followUser(validFollowerId, validFolloweeId);

        verify(subscriptionRepository).followUser(validFollowerId, validFolloweeId);
        verify(followerEventPublisher, times(1)).publish(any(FollowerEventDto.class));
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionExists() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(validFollowerId, validFolloweeId))
                .thenReturn(Boolean.TRUE);

        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(validFollowerId, validFolloweeId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUnfollowUserIdsAreInvalid() {
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
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.unfollowUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenFollowUserIdsAreInvalid() {
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
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.followUser(sameUserId, sameUserId));

        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void shouldReturnFollowingCountWhenFollowerIdIsValid() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(validFollowerId))
                .thenReturn(5);

        int followingCount = subscriptionService.getFollowingCount(validFollowerId);

        assertEquals(5, followingCount);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(validFollowerId);
    }

    @Test
    void shouldThrowExceptionWhenFollowerIdIsInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowingCount(invalidFollowerId));

        verify(subscriptionRepository, never()).findFolloweesAmountByFollowerId(anyLong());
    }

    @Test
    void shouldReturnFollowersCountWhenFolloweeIdIsValid() {
        when(subscriptionRepository.findFollowersAmountByFolloweeId(validFolloweeId))
                .thenReturn(5);

        int followersCount = subscriptionService.getFollowersCount(validFolloweeId);

        assertEquals(5, followersCount);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(validFolloweeId);
    }

    @Test
    void shouldThrowExceptionWhenFolloweeIdIsInvalid() {
        assertThrows(
                DataValidationException.class,
                () -> subscriptionService.getFollowersCount(invalidFolloweeId));

        verify(subscriptionRepository, never()).findFollowersAmountByFolloweeId(anyLong());
    }

}