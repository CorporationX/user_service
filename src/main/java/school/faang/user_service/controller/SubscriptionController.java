package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("following")
    public void followUser(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.followUser(subscriptionRequestDto);
    }

    @PostMapping("unfollowing")
    public void unfollowUser(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.unfollowUser(subscriptionRequestDto);
    }

    @GetMapping("followers/{followeeId}")
    public List<UserDto> getFollowers(
            @PathVariable long followeeId,
            @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("followings/{followerId}")
    public List<UserDto> getFollowings(
            @PathVariable long followerId,
            @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowings(followerId, filter);
    }

    @GetMapping("followers/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("followings/count/{followerId}")
    public int getFollowingsCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingsCount(followerId);
    }
}
