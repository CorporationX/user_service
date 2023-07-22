package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUser_ThrowsExceptionOnExistingSubscription() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(anyLong(), anyLong());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUser_ThrowsExceptionOnSelfSubscription() {
        long followerId = 1L;

        assertThrows(DataValidException.class, () -> subscriptionService.followUser(followerId, followerId));
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void testFollowUser_CallsRepositoryOnValidSubscription() {
        long followerId = 1L;
        long followeeId = 2L;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(false);

        subscriptionService.followUser(followerId, followeeId);

        Mockito.verify(subscriptionRepository, Mockito.times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }
}
