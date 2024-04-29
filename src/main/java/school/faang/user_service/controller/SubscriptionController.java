package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("The user " + followeeId +
                    " tried to follow himself!");
        }
        service.followUser(followerId, followeeId);
    }
}
