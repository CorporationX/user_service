package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator subscriptionValidator;
    private final UserFilterDtoValidator userFilterDtoValidator;

    @PostMapping("/{followerId}")
    public void followUser(@PathVariable long followerId, @RequestParam long followeeId) {
        subscriptionValidator.checkFollowerAndFolloweeAreDifferent(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}")
    public void unfollowUser(@PathVariable long followerId, @RequestParam long followeeId) {
        subscriptionValidator.checkFollowerAndFolloweeAreDifferent(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filters) {
        subscriptionValidator.checkIdIsCorrect(followeeId);
        userFilterDtoValidator.checkUserFilterDtoIsNull(filters);

        return subscriptionService.getFollowers(followeeId, filters);
    }

    @GetMapping("/followers/{followeeId}/count")
    public long getFollowersCount(@PathVariable long followeeId) {
        subscriptionValidator.checkIdIsCorrect(followeeId);

        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following/{followerId}")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filters) {
        subscriptionValidator.checkIdIsCorrect(followerId);
        userFilterDtoValidator.checkUserFilterDtoIsNull(filters);

        return subscriptionService.getFollowing(followerId, filters);
    }

    @GetMapping("/following/{followerId}/count")
    public long getFollowingCount(@PathVariable long followerId) {
        subscriptionValidator.checkIdIsCorrect(followerId);

        return subscriptionService.getFollowingCount(followerId);
    }
}
