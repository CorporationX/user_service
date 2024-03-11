package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

import java.util.List;

public interface SubscriptionService {

    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    int getFollowingCount(long followerId);

    int getFollowersCount(long followeeId);

    List<UserDto> getFollowings(long followerId, UserFilterDto filter);

    List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto);
}
