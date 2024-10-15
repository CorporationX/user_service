package school.faang.user_service.service;

import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    List<UserDto> getFollowers(long followeeId, UserFilterDto filters);

    int getFollowersCount(long followeeId);

    List<UserDto> getFollowing(long followerId, UserFilterDto filters);

    int getFollowingCount(long followerId);
}
