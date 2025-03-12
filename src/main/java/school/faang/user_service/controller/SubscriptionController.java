package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.FollowerResponse;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "{followerId}/follow/{followeeId}")
    public void followUser(@NotNull @PathVariable("followerId") Long followerId,
                           @NotNull @PathVariable("followeeId") Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping(value = "{followerId}/unfollow/{followeeId}")
    public void unfollowUser(@NotNull @PathVariable("followerId") Long followerId,
                             @NotNull @PathVariable("followeeId") Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping(value = "/followers/{id}")
    public List<FollowerResponse> getFollowing(@NotNull @PathVariable("id") Long id, UserFilterRequest filter) {
        return subscriptionService.getFollowing(id, filter);
    }

    @GetMapping(value = "/followers/{id}/count")
    public int getFollowingCount(@NotNull @PathVariable("id") Long id) {
        return subscriptionService.getFollowingCount(id);
    }

    @GetMapping(value = "/followers/users/{id}")
    public List<FollowerResponse> getFollowers(@NotNull @PathVariable("id") Long id, UserFilterRequest filter) {
        return subscriptionService.getFollowers(id, filter);
    }

    @GetMapping(value = "/followers/users/{id}/count")
    public int getFollowersCount(@NotNull @PathVariable Long id) {
        return subscriptionService.getFollowersCount(id);
    }
}