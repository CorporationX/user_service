package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;
import java.util.Optional;

import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIBE_ITSELF_VALIDATION;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/followers/{followerId}")
    public void followUser(@PathVariable Long followerId,
                           @PathVariable("userId") Long followeeId) {
        subscriptionValidation(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/followers/{followerId}")
    public void unfollowUser(@PathVariable Long followerId,
                             @PathVariable("userId") Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<SubscriptionUserDto> getFollowers(@PathVariable("userId") Long followeeId,
                                                  @RequestBody Optional<UserFilterDto> filters) {
        return subscriptionService.getFollowers(followeeId, filters.orElse(new UserFilterDto()));
    }

    @GetMapping("/followers/count")
    public int getFollowersCount(@PathVariable("userId") Long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followings")
    public List<SubscriptionUserDto> getFollowing(@PathVariable("userId") Long followerId,
                                                  @RequestBody Optional<UserFilterDto> filters) {
        return subscriptionService.getFollowing(followerId, filters.orElse(new UserFilterDto()));
    }

    @GetMapping("/followings/count")
    public int getFollowingCount(@PathVariable("userId") Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    private void subscriptionValidation(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new DataValidationException(SUBSCRIBE_ITSELF_VALIDATION.getMessage());
        }
    }
}
