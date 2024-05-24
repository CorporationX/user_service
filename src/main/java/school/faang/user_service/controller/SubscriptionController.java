package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator subscriptionValidator;
    private final UserFilterDtoValidator userFilterDtoValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.checkFollowerAndFolloweeAreDifferent(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.checkFollowerAndFolloweeAreDifferent(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        userFilterDtoValidator.checkUserFilterDtoIsNull(filters);
        return subscriptionService.getFollowers(followeeId, filters);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        userFilterDtoValidator.checkUserFilterDtoIsNull(filters);
        return subscriptionService.getFollowing(followerId, filters);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
