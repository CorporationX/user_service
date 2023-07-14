package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
        sameUserValidation(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @GetMapping("/followers/count/{id}")
    public int getFollowersCount(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    private static void sameUserValidation(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself.");
        }
    }
}
