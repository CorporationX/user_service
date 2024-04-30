package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequestMapping("/follow")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("The user " + followeeId +
                    " tried to follow himself!");
        }
        service.followUser(followerId, followeeId);
    }
}
