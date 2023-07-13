package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.commonMessages.ErrorMessages;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    ErrorMessages errorMessages = new ErrorMessages();

    public void followUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter){
        validateUserId(followeeId);
        return new UserFilter().applyFilter(subscriptionRepository.findByFolloweeId(followeeId).toList(), filter);
    }

    public int getFollowersCount(long followeeId){
        validateUserId(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter){
        validateUserId(followeeId);
        return new UserFilter().applyFilter(subscriptionRepository.findByFolloweeId(followeeId).toList(), filter);
    }

    public int getFollowingCount(long followerId){
        validateUserId(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateFollower(long followerId, long followeeId){
        validateUserId(followerId);
        validateUserId(followeeId);
        if(followerId == followeeId){
            throw new DataValidationException(errorMessages.SAMEID);
        }
    }

    private void validateUserId(long userId){
        if(userId <= 0){
            throw new IllegalArgumentException(errorMessages.NEGATIVEID);
        }
    }
}
