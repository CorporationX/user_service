package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long id, UserFilterDto filter) {
        return subscriptionService.getFollowers(id, filter);
    }

    public List<UserDto> getFollowing(long id, UserFilterDto filter) {
        return subscriptionService.getFollowing(id, filter);
    }

    public long getFollowersCount(long id) {
        return subscriptionService.getFollowersCount(id);
    }

    public long getFollowingCount(long id) {
        return subscriptionService.getFollowingCount(id);
    }
}