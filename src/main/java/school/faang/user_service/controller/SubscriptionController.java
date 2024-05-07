package school.faang.user_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.error.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
public class SubscriptionController {
    @Autowired
    private SubscriptionService service;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на самого себя");
        }
        service.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться от самого себя");
        }
        service.unfollowUser(followerId, followeeId);
    }


    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return service.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return service.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return service.getFollowing(followeeId, filter);
    }

    public int getFollowingCount(long followerId) {
        return service.getFollowingCount(followerId);
    }

}
