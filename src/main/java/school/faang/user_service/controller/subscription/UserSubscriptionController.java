package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.subscription.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.subscription.UserSubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    public void followUser(long followeeId) {
        ensureUserIdValid(followeeId);
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followeeId) {
        ensureUserIdValid(followeeId);
        long followerId = userContext.getUserId();
        ensureFollowerAndFolloweeNotTheSameUser(followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    public CountResponse getFollowersCount(long followeeId) {
        ensureUserIdValid(followeeId);
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    public CountResponse getFolloweesCount(long followerId) {
        ensureUserIdValid(followerId);
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    public List<UserDto> getFollowers(long followeeId) {
        ensureUserIdValid(followeeId);
        return userSubscriptionService.getFollowers(followeeId);
    }

    public List<UserDto> getFollowees(long followerId) {
        ensureUserIdValid(followerId);
        return userSubscriptionService.getFollowees(followerId);
    }

    private void ensureUserIdValid(long userId) {
        if (userId <= 0) {
            throw new DataValidationException("user id cannot be equal to or less than zero");
        }
    }

    private void ensureFollowerAndFolloweeNotTheSameUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException("user cannot follow or unfollow himself");
        }
    }
}
