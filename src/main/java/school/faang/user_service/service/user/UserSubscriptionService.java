package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;

import java.util.List;

public interface UserSubscriptionService {
    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    CountResponse getFollowersCount(long followeeId);

    CountResponse getFolloweesCount(long followerId);

    List<UserDto> getFollowers(long followeeId, UserFiltersDto filters);

    List<UserDto> getFollowees(long followerId, UserFiltersDto filters);
}
