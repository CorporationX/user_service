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
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee cannot be the same");
        } else if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("User IDs cannot be negative");
        } else {
            subscriptionService.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee cannot be the same");
        } else if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("User IDs cannot be negative");
        } else {
            subscriptionService.unfollowUser(followerId, followeeId);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (followeeId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowingCount(long followeeId) {
        if (followeeId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowingCount(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        if (followerId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowing(followerId, filter);
    }

    public int getFollowersCount(long followerId) {
        if (followerId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowersCount(followerId);
    }
}
