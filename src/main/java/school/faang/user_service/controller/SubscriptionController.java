package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId){
        validateFollower(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter){
        return subscriptionService.getFollowers(followeeId, filter);
    }

    private boolean validateFollower(long followerId, long followeeId){
        if(followerId == followeeId){
            throw new DataValidationException("Пользователь пытается подписаться сам на себя");
        }
        return true;
    }
}
