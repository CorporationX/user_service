package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static school.faang.user_service.exceptions.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exceptions.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@Component
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if(followerId == followeeId) {
            throw new DataValidationException(USER_FOLLOWING_HIMSELF_EXCEPTION.getMessage());
        }

        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if(followerId == followeeId) {
            throw new DataValidationException(USER_UNFOLLOWING_HIMSELF_EXCEPTION.getMessage());
        }

        subscriptionService.unfollowUser(followerId, followeeId);
    }
}
