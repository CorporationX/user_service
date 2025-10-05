package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/users/{followeeId}/follow")
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/users/{followeeId}/unfollow")
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/users/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/users/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/users/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, UserFiltersDto filters) {
        return userSubscriptionService.getFollowers(followeeId, filters);
    }

    @GetMapping("/users/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable long followerId, UserFiltersDto filters) {
        return userSubscriptionService.getFollowees(followerId, filters);
    }
}
