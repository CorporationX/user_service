package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor


public class UserSubscriptionController {
    private final UserSubscriptionService subscriptionService;
    private final UserContext userContext;

    public void followUser(long id) {
        subscriptionService.followUser(id, userContext.getUserId());

    }

    public void unfollowUser(long followeeId) {
        subscriptionService.unfollowUser(followeeId, userContext.getUserId());
    }

    public CountResponse getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public CountResponse getFolloweesCount(long followeesId) {
        return subscriptionService.getFolloweesCount(followeesId);
    }

    public List<UserDto> getFollowers(long followerId) {
        return subscriptionService.getFollowers(followerId);
    }

    public List<UserDto> getFollowees(long followeeId) {
        return subscriptionService.getFollowees(followeeId);
    }
}
