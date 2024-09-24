package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUserById(@RequestParam("followerId") long followerId,
                               @RequestParam("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUserById(@RequestParam("followerId") long followerId,
                                 @RequestParam("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/followers")
    public List<UserDto> getFollowers(@RequestParam("followeeId") long followeeId,
                                      @RequestBody(required = false) UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public int getFollowersCount(@RequestParam("followerId") long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/following/count")
    public int getFollowingCount(@RequestParam("followerId") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    @PostMapping("/following")
    public List<UserDto> getFollowing(@RequestParam("followeeId") long followeeId,
                                      @RequestBody(required = false) UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }
}
