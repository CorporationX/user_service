package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(value = {MockitoExtension.class})
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    private SubscriptionController subscriptionController;

    @BeforeEach
    public void setUp() {
        subscriptionController = new SubscriptionController(subscriptionService);
    }

    @Test
    public void selfSubscribe() {
        long followerId = new Random().nextLong();
        User user1 = User.builder().id(followerId + 1).build();
        assertThrows(DataValidationException.class, () -> subscriptionController.followUser(user1.getId(), user1.getId()));
    }

    @Test
    public void userFollowedSuccess() {
        long followerId = new Random().nextLong();
        long followeeId = new Random().nextLong();
        User user1 = User.builder().id(followerId + 1).build();
        User user2 = User.builder().id(followeeId + 1).build();
        subscriptionController.followUser(user1.getId(), user2.getId());
        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(user1.getId(), user2.getId());
    }

}
