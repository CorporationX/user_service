package school.faang.user_service.controller.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator validator;

    @PostMapping("/{follower_id}/{followee_id}")
    public void followUser(@PathVariable("follower_id") @Positive long followerId,
                           @PathVariable("followee_id") @Positive long followeeId) {
        validator.checkingSubscription(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{follower_id}/{followee_id}")
    public void unfollowUser(@PathVariable("follower_id") @Positive long followerId,
                             @PathVariable("followee_id") @Positive long followeeId) {
        validator.checkingSubscription(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followee_id}/followers")
    public List<UserDto> getFollowers(@PathVariable("followee_id") @Positive long followeeId,
                                      @Validated UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/{followee_id}/followers_count")
    public int getFollowersCount(@PathVariable("followee_id") @Positive long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{follower_id}/follows")
    public List<UserDto> getFollowing(@PathVariable("follower_id") @Positive long followerId, @NotNull UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/{follower_id}/follows_count")
    public int getFollowingCount(@PathVariable("follower_id") @Positive long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
