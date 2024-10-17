package school.faang.user_service.service;

import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.filter_dto.UserFilterDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    List<UserDto> getFollowers(long followeeId, UserFilterDto filter);

    long getFollowersCount(long followeeId);

    List<UserDto> getFollowing(long followerId, UserFilterDto filter);

    long getFollowingCount(long followerId);
}
