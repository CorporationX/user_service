package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Can't follow yourself");
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!(followerId != followeeId)) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Can't unfollow yourself");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return userMapper.toDto(subscriptionService.getFollowers(followeeId, filter));
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return userMapper.toDto(subscriptionService.getFollowing(followeeId, filter));
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
