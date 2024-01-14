package school.faang.user_service.controller.subscription;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.Map;

@RestController
@RequestMapping("subscriptions")
@RequiredArgsConstructor
public class SubscriptionControllerImpl {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public Map.Entry<String, Boolean> followUser(@PathParam("followerId") long followerId,
                                                 @PathParam("followeeId") long followeeId) {
        validateUserIds(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        return Map.entry("isFollowed", true);
    }

    @DeleteMapping("/unfollow")
    public Map.Entry<String, Boolean> unfollowUser(@PathParam("followerId") long followerId,
                                                   @PathParam("followeeId") long followeeId) {
        validateUserIds(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        return Map.entry("isUnfollowed", true);
    }

    private void validateUserIds(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("User identifiers must be positive numbers");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee the same user");
        }
    }

}
