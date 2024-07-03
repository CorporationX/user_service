package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("User cannot follow to himself");
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("User cannot unfollow to himself");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public Integer getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public Integer getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
