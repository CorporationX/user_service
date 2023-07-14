package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    private static final String UNSUBSCRIBE_YOURSELF_EXCEPTION = "You can't unsubscribe from yourself.";
    private static final String SUBSCRIBE_YOURSELF_EXCEPTION = "You can't subscribe to yourself.";

    @PutMapping("/follow/{id}")
    public void followUser(@RequestParam("followerId") long followerId,
                           @PathVariable("id") long followeeId) {
        sameUserValidation(followerId, followeeId, SUBSCRIBE_YOURSELF_EXCEPTION);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow/{id}")
    public void unfollowUser(@RequestParam("followerId") long followerId,
                         @PathVariable("id") long followeeId) {
        sameUserValidation(followerId, followeeId, UNSUBSCRIBE_YOURSELF_EXCEPTION);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/count/{id}")
    public int getFollowersCount(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    private void sameUserValidation(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}
