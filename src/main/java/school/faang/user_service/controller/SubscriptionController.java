package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        validateFollow(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateFollow(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFolloweesCount(followerId);
    }

    public void validateFollow(long firstId, long secondId) {
        if (firstId <= 0 || secondId <= 0) {
            throw new DataValidationException("Id cannot be less 0! ");
        }
        if (firstId == secondId) {
            throw new DataValidationException("Follower and followee can not be the same");
        }
    }
}
