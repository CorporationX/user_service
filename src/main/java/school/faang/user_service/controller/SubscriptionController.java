package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestParam(name = "followerId") long followerId,
                           @RequestParam(name = "followeeId") long followeeId) {

        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam(name = "followerId") long followerId,
                             @RequestParam(name = "followeeId") long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot unfollow yourself");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@RequestParam(name = "followeeId") long followeeId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public long getFollowersCount(@RequestParam(name = "followeeId") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following")
    public List<UserDto> getFollowing(@RequestParam(name = "followerId") long followerId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/following/count")
    public long getFollowingCount(@RequestParam(name = "followerId") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
