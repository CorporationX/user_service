package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;
    private final UserContext userContext;
    private final SubscriptionValidator validator;

    @Transactional
    @PostMapping("/user/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        validator.validateUserTriedHimself(followerId, followeeId);
        service.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("The user " + followeeId +
                    " tried to unfollow himself!");
        }
        service.unfollowUser(followerId, followeeId);
    }
}
