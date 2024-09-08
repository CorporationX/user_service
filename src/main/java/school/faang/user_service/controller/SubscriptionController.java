package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        if (!(followerId == followeeId)) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("You can't follow yourself.");
        }
    }

    @SneakyThrows
    public void unfollowUser(long followerId, long followeeId) {
        if (!(followerId == followeeId)) {
            subscriptionService.unFollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("You can't unfollow yourself.");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public void getFollowersCount(long followerId) {
        subscriptionService.getFollowersCount(followerId);
    }


    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public void getFollowingCount(long followerId) {
        subscriptionService.getFollowingCount(followerId);
    }
}
