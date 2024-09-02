package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Component
@AllArgsConstructor
public class SubscriptionController {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);
    private SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        try {
            subscriptionService.followUser(followerId, followeeId);
            log.info("Follower {} followed user with id {}.", followerId, followeeId);
        } catch (DataValidationException exception) {
            log.error(exception.getMessage());
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        try {
            subscriptionService.unfollowUser(followerId, followeeId);
            log.info("Follower {} unfollowed user with id {}.", followerId, followeeId);
        } catch (DataValidationException exception) {
            log.error(exception.getMessage());
        }
    }
}
