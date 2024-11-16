package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't follow itself");

        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        isFollowerFolloweeIdsEqual(followerId, followeeId, "Follower can't unfollow itself");

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestParam UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followings/{followeeId}")
    public List<UserDto> getFollowing(@PathVariable long followeeId, @RequestParam UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/followings/count/{followeeId}")
    public int getFollowingCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping("/{followeeId}/followers/{followerId}")
    public boolean checkFollowerOfFollowee(@PathVariable long followeeId, @PathVariable long followerId) {
        return subscriptionService.checkFollowerOfFollowee(followeeId, followerId);
    }

    private void isFollowerFolloweeIdsEqual(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}