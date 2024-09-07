package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;

import java.util.List;

@Component
public interface SubscriptionService {

    void followUser(long followerId, long followeeId) throws DataValidationException;

    void unfollowUser(long followerId, long followeeId) throws DataValidationException;

    List<UserDto> getFollowers(long followeeId, UserFilterDto filters);

    List<UserDto> getFollowing(long followeeId, UserFilterDto filters);

    int getFollowersCount(long followerId);

    int getFollowingCount(long followerId);
}