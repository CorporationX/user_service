package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}")
    public void followUser(@PathVariable @Positive(message = "Id should be positive")
                           long followerId,
                           @PathVariable("userId") @Positive(message = "Id should be positive")
                           long followeeId) {

        if (followerId == followeeId) {
            throw new DataValidationException(MessageError.SELF_FOLLOWING);
        }

        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}")
    public void unfollowUser(@PathVariable @Positive long followerId, @PathVariable("userId") @Positive long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(MessageError.SELF_FOLLOWING);
        }

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping
    public List<UserDto> getFollowers(@PathVariable("userId") @Positive long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/count")
    public int getFollowersCount(@PathVariable("userId") @Positive long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/count-followees")
    public int getFolloweesCount(@PathVariable("userId") @Positive long followerId) {
        return subscriptionService.getFolloweesCount(followerId);
    }
}
