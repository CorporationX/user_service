package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/followUser/{followerId}/{followeeId}")
    public void followUser(@PathVariable(name = "followerId") long followerId,
                           @PathVariable(name = "followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
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

    @GetMapping("/{userId}")
    public List<Long> getFollowersIds(@PathVariable long userId) {
        return subscriptionService.getFollowersIds(userId);
    }

    @GetMapping("/{followeeId}")
    public List<Long> getFollowingIds(@PathVariable long followeeId) {
        return subscriptionService.getFollowingIds(followeeId);
    }
}
