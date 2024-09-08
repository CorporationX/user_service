package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validators.SubscriptionValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator validator;

    public void followUser(long followerId, long followeeId) {
        validator.checkingSubscription(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validator.checkingSubscription(followerId, followeeId);
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

}
