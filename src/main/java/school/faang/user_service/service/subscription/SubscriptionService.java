package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(Long followerId, Long followeeId);

    void unfollowUser(Long followerId, Long followeeId);

    List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto filters);

    int getFollowersCount(Long followeeId);

    List<SubscriptionUserDto> getFollowings(Long followerId, UserFilterDto filters);

    int getFollowingCounts(Long followerId);
}
