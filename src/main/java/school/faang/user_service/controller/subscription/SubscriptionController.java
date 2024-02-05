package school.faang.user_service.controller.subscription;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

@RestController
@RequestMapping("subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public Map.Entry<String, Boolean> followUser(@PathParam("followerId") long followerId,
                                                 @PathParam("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return Map.entry("isFollowed", true);
    }

    @DeleteMapping("/unfollow")
    public Map.Entry<String, Boolean> unfollowUser(@PathParam("followerId") long followerId,
                                                   @PathParam("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return Map.entry("isUnfollowed", true);
    }

    @GetMapping("/count/followings/{followerId}")
    public Map.Entry<String, Integer> getFollowingCount(@PathVariable("followerId") long followerId) {
        int followingCount = subscriptionService.getFollowingCount(followerId);
        return Map.entry("followingCount", followingCount);
    }

    @GetMapping("/count/followers/{followeeId}")
    public Map.Entry<String, Integer> getFollowersCount(@PathVariable("followeeId") long followeeId) {
        int followersCount = subscriptionService.getFollowersCount(followeeId);
        return Map.entry("followersCount", followersCount);
    }

}
