package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionControllerValidator validator;


    @PostMapping("/follow")
    public void followUser(@RequestParam("followerId") long followerId,
                           @RequestParam("followeeId") long followeeId) {
        validator.validate(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validator.validate(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);

    }
}
