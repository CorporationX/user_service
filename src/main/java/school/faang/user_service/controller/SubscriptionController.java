package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/following/{followerId}")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @PostMapping("/follow/{followerId}/{followeeId}")
    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow/{followerId}/{followeeId}")
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/{followerId}/count")
    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/following/{followerId}/count")
    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}