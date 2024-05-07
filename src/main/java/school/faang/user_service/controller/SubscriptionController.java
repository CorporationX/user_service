package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@Controller
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        checkUserDifference(followerId, followeeId, USER_FOLLOWING_HIMSELF_EXCEPTION);

        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        checkUserDifference(followerId, followeeId, USER_UNFOLLOWING_HIMSELF_EXCEPTION);

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    private void checkUserDifference(long followerId, long followeeId, ExceptionMessage exceptionMessage) {
        if (followerId == followeeId) {
            throw new DataValidationException(exceptionMessage.getMessage());
        }
    }
}
