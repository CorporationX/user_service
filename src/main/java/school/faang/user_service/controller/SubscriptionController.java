package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
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

    @PutMapping("/follow/{id}")
    public void followUser(@RequestParam("followerId") long followerId,
                           @PathVariable("id") long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself.");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow/{id}")
    public void unfollowUser(@RequestParam("followerId") long followerId,
                         @PathVariable("id") long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't unsubscribe from yourself.");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }
}
