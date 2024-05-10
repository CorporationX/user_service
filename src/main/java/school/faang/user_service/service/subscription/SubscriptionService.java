package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;

import java.util.List;

public interface SubscriptionService {

    void followUser(SubscriptionRequestDto subscriptionRequestDto);

    void unfollowUser(SubscriptionRequestDto subscriptionRequestDto);

    List<UserDto> getFollowers(long followeeId, UserFilterDto filter);

    List<UserDto> getFollowings(long followerId, UserFilterDto filter);

    int getFollowersCount(long followeeId);

    int getFollowingsCount(long followerId);
}
