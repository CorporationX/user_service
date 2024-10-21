package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.CountDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@Component
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @SneakyThrows
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unFollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public CountDto getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }


    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public CountDto getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
