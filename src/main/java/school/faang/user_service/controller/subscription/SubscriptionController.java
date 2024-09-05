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
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/followUser")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@RequestParam("followerId") long followerId,
                           @RequestParam("followeeId") long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollowUser")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@RequestParam("followerId") long followerId,
                             @RequestParam("followeeId") long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/getFollowers")
    public List<UserDto> getFollowers(@RequestParam("followeeId") long followeeId,
                                      @RequestBody(required = false) UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/getFollowersCount")
    public int getFollowersCount(@RequestParam("followerId") long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("/getFollowingCount")
    public int getFollowingCount(@RequestParam("followerId") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    @GetMapping("/getFollowing")
    public List<UserDto> getFollowing(@RequestParam("followeeId") long followeeId,
                                      @RequestBody(required = false) UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }
}
