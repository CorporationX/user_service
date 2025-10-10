package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    public void followUser(long followeeId) {
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
