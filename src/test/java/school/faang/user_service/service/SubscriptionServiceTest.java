package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(value = {MockitoExtension.class})
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository);
    }

    @Test
    public void subscribeWhen_SubscriptionExists() {
        long followerId = new Random().nextLong();
        long followeeId = new Random().nextLong();
        User user1 = User.builder().id(followerId + 1).build();
        User user2 = User.builder().id(followeeId + 1).build();
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(user1.getId(), user2.getId())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(user1.getId(), user2.getId()));
    }

    @Test
    public void userFollowedSuccess() {
        long followerId = new Random().nextLong();
        long followeeId = new Random().nextLong();
        User user1 = User.builder().id(followerId + 1).build();
        User user2 = User.builder().id(followeeId + 1).build();
        subscriptionService.followUser(user1.getId(), user2.getId());
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(user1.getId(), user2.getId());
    }
}
