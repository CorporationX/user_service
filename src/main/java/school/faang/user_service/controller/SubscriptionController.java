package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        validator.validateUserTriedFollowHimself(followerId, followeeId);
        service.followUser(followerId, followeeId);
    }

    @Transactional
    @DeleteMapping("/user/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        validator.validateUserTriedUnfollowHimself(followerId, followeeId);
        service.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    @GetMapping("/followers/count")
    public ResponseEntity<Integer> getFollowersCount() {
        long followerId = userContext.getUserId();
        int count =  service.getFollowersCount(followerId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
