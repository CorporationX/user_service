package school.faang.user_service.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

@Tag(name = "Subscription", description = "The Subscription API")
@RestController
@RequestMapping("subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Подписка на пользователя",
            description = "Подписываемся на пользователя с помощью ID"
    )
    @PostMapping("/follow")
    public Map.Entry<String, Boolean> followUser(@PathParam("followerId") long followerId,
                                                 @PathParam("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return Map.entry("isFollowed", true);
    }

    @Operation(
            summary = "Отписка от пользователя",
            description = "Отписываемся от пользователя с помощью ID"
    )
    @DeleteMapping("/unfollow")
    public Map.Entry<String, Boolean> unfollowUser(@PathParam("followerId") long followerId,
                                                   @PathParam("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return Map.entry("isUnfollowed", true);
    }

    @Operation(
            summary = "Количество подписок пользователя",
            description = "Показывает количество подписок пользователя по его ID"
    )
    @GetMapping("/count/followings/{followerId}")
    public Map.Entry<String, Integer> getFollowingCount(@PathVariable("followerId") long followerId) {
        int followingCount = subscriptionService.getFollowingCount(followerId);
        return Map.entry("followingCount", followingCount);
    }

    @Operation(
            summary = "Количество подписчиков пользователя",
            description = "Показывает количество подписчиков пользователя по его ID"
    )
    @GetMapping("/count/followers/{followeeId}")
    public Map.Entry<String, Integer> getFollowersCount(@PathVariable("followeeId") long followeeId) {
        int followersCount = subscriptionService.getFollowersCount(followeeId);
        return Map.entry("followersCount", followersCount);
    }

}
