package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public void followUser(@RequestParam(name = "followerId") long followerId,
                           @RequestParam(name = "followeeId") long followeeId) {

        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        }
        subscriptionService.followUser(followerId, followeeId);
    }
}
