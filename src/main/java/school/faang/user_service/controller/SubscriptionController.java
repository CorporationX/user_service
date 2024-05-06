package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validation.SubscriptionValidator;

import java.util.List;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserContext userContext;
    private final SubscriptionValidator subscriptionValidator;

    @DeleteMapping("/user/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        subscriptionValidator.validateUser(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/count")
    public int getFollowersCount() {
        long followerId = userContext.getUserId();
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping("/user/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        subscriptionValidator.validateUser(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(
            @PathVariable long followeeId, @RequestBody UserFilterDto filters) {
        return subscriptionService.getFollowers(followeeId, filters);
    }
}
