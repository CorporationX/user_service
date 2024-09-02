package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

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
            log.warn(exception.getMessage());
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        try {
            subscriptionService.unfollowUser(followerId, followeeId);
            log.info("Follower {} unfollowed user with id {}.", followerId, followeeId);
        } catch (DataValidationException exception) {
            log.warn(exception.getMessage());
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
