package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.EntityCountDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.dto.subscription.responses.SuccessResponse;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{followeeId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/followers/{followerId}")
    public SuccessResponse followUser(@PathVariable Long followerId,
                                      @PathVariable Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return new SuccessResponse("User with id %d now follows user with id %d"
                .formatted(followerId, followeeId));
    }

    @DeleteMapping("/followers/{followerId}")
    public SuccessResponse unfollowUser(@PathVariable Long followerId,
                                        @PathVariable Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return new SuccessResponse("User with id %d cancel following on user with id %d"
                .formatted(followerId, followeeId));
    }

    @GetMapping("/followers")
    public List<SubscriptionUserDto> getFollowers(@PathVariable Long followeeId,
                                                  @RequestBody UserFilterDto filters) {
        return subscriptionService.getFollowers(followeeId, filters);
    }

    @GetMapping("/followers/count")
    public EntityCountDto getFollowersCount(@PathVariable Long followeeId) {
        int count = subscriptionService.getFollowersCount(followeeId);
        return new EntityCountDto(count);
    }

    @GetMapping("/followings")
    public List<SubscriptionUserDto> getFollowing(@PathVariable Long followerId,
                                                  @RequestBody UserFilterDto filters) {
        return subscriptionService.getFollowings(followerId, filters);
    }

    @GetMapping("/followings/count")
    public EntityCountDto getFollowingCount(@PathVariable Long followerId) {
        int count = subscriptionService.getFollowingCounts(followerId);
        return new EntityCountDto(count);
    }
}
