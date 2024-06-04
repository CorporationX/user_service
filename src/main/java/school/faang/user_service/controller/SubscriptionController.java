package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscriptions")
@Tag(name = "Subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Follow user")
    @PostMapping("following")
    public void followUser(@ParameterObject @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.followUser(subscriptionRequestDto);
    }

    @Operation(summary = "Unfollow user")
    @PostMapping("unfollowing")
    public void unfollowUser(@ParameterObject @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.unfollowUser(subscriptionRequestDto);
    }

    @Operation(summary = "Get all followers by followeeId with filters")
    @GetMapping("followers/{followeeId}/filter")
    public List<UserDto> getFollowersWithFilter(
            @Parameter @PathVariable long followeeId,
            @ParameterObject @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Get all followers by followeeId")
    @GetMapping("followers/{followeeId}")
    public List<UserDto> getFollowers(@Parameter @PathVariable long followeeId) {
        return subscriptionService.getFollowers(followeeId, new UserFilterDto());
    }

    @Operation(summary = "Get all followings by followerId with filter")
    @GetMapping("followings/{followerId}/filter")
    public List<UserDto> getFollowingsWithFilter(
            @Parameter @PathVariable long followerId,
            @ParameterObject @RequestBody(required = false) UserFilterDto filter
    ) {
        return subscriptionService.getFollowings(followerId, filter);
    }

    @Operation(summary = "Get all followings by followerId")
    @GetMapping("followings/{followerId}")
    public List<UserDto> getFollowings(@Parameter @PathVariable long followerId) {
        return subscriptionService.getFollowings(followerId, new UserFilterDto());
    }

    @Operation(summary = "Get followers count by foloweeId")
    @GetMapping("followers/count/{followeeId}")
    public int getFollowersCount(@Parameter @PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Get followings count by folowerId")
    @GetMapping("followings/count/{followerId}")
    public int getFollowingsCount(@Parameter @PathVariable long followerId) {
        return subscriptionService.getFollowingsCount(followerId);
    }
}
