package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void testFollowUserThrowsExceptionWhenFollowsItself() {
        User user1 = new User();
        Long userId1 = 1000L;
        user1.setId(userId1);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId1));
    }

    @Test
    public void testFollowUser() {
        User user1 = new User();
        User user2 = new User();
        Long userId1 = 1000L;
        Long userId2 = 2000L;
        user1.setId(userId1);
        user2.setId(userId2);
//        when(subscriptionRepository.findById(userId1)).thenReturn(Optional.of(user1));
//        when(subscriptionRepository.findById(userId2)).thenReturn(Optional.of(user2));
//        when(subscriptionRepository.followUser(userId1, userId2)).thenReturn(Optional.of(user1));
//        Mockito.verify(subscriptionRepository, times(1)).followUser(userId1, userId2);
    }
}
