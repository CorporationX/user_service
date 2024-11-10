package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private SubscriptionController subscriptionController = new SubscriptionController(null);

    private long followerId = 1l;

    @Test
    public void testIfThrowsExceptionWhenUserFollowsHimself() {
        assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followerId));
    }

    @Test
    public void testIfThrowsExceptionWhenUserUnfollowsHimself() {
        assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followerId));
    }

    @Test
    public void testIfThrowsExceptionWhenUserFollowsNegativeId() {
        assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, -1));
    }
}
