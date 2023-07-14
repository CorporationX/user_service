package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true).thenReturn(false);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);

        verifyNoMoreInteractions(subscriptionRepository);
    }
}