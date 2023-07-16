package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFollowUser_ThrowsExceptionOnExistingSubscription() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());

    }

    @Test
    public void testFollowUser_ThrowsExceptionOnSelfSubscription() {
        long followerId = 1;

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followerId));
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUser_CallsRepositoryOnValidSubscription() {
        long followerId = 1;
        long followeeId = 2;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);

    }
}
