package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscriptions")
@Tag(name = "Subscriptions", description = "Subscription API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Follow user")
    @PostMapping("following")
    public void followUser(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.followUser(subscriptionRequestDto);
    }

    @Operation(summary = "Unfollow user")
    @PostMapping("unfollowing")
    public void unfollowUser(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.unfollowUser(subscriptionRequestDto);
    }

    @Operation(summary = "Get all followers by followeeId")
    @GetMapping("followers/{followeeId}")
    public List<UserDto> getFollowers(
            @PathVariable long followeeId,
            @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Get all followings by followerId")
    @GetMapping("followings/{followerId}")
    public List<UserDto> getFollowings(
            @PathVariable long followerId,
            @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowings(followerId, filter);
    }

    @Operation(summary = "Count followers of user with followeeId")
    @GetMapping("followers/count/{followeeId}")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Count followings of user with followerId")
    @GetMapping("followings/count/{followerId}")
    public int getFollowingsCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingsCount(followerId);
    }
}
