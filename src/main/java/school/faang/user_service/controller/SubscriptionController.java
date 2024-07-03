package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}")
    public void followUser(@PathVariable @Positive long followerId, @PathVariable("userId") @Positive long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Unable to follow yourself");
        }

        subscriptionService.followUser(followerId, followeeId);
    }

}
