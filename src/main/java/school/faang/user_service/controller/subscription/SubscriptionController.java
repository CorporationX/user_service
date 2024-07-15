package school.faang.user_service.controller.subscription;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.message.ExceptionMessage;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static school.faang.user_service.exception.message.ExceptionMessage.USER_FOLLOWING_HIMSELF_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.USER_UNFOLLOWING_HIMSELF_EXCEPTION;

@RestController
@RequestMapping("subscriptions/")
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        checkUserDifference(followerId, followeeId, USER_FOLLOWING_HIMSELF_EXCEPTION);

        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        checkUserDifference(followerId, followeeId, USER_UNFOLLOWING_HIMSELF_EXCEPTION);

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("followers/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("followers/{followeeId}")
    public List<Long> getFollowersIds(@PathVariable long followeeId) {
        return subscriptionService.getFollowersIds(followeeId);
    }

    @GetMapping("followers/{followeeId}/count")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @PostMapping("followings/{followerId}")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("followings/{followerId}/count")
    public int getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    private void checkUserDifference(long followerId, long followeeId, ExceptionMessage exceptionMessage) {
        if (followerId == followeeId) {
            throw new DataValidationException(exceptionMessage.getMessage());
        }
    }
}
