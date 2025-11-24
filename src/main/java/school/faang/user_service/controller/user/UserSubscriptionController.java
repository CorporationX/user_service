package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @GetMapping("/{followeeId}")
    public void followUser(@PathVariable long followeeId) {
        long userId = userContext.getUserId();
        userSubscriptionService.followUser(userId, followeeId);
    }

    public void unfollowUser(long followeeId) {
        long userId = userContext.getUserId();
        userSubscriptionService.unfollowUser(userId, followeeId);
    }

    public CountResponseDto getFollowersCount(long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    public CountResponseDto getFolloweesCount(long followeeId) {
        return userSubscriptionService.getFolloweesCount(followeeId);
    }

    public List<UserDto> getFollowers(long followeeId) {
        return userSubscriptionService.getFollowers(followeeId);
    }

    public List<UserDto> getFollowees(long followerId) {
        return userSubscriptionService.getFollowees(followerId);
    }

}
