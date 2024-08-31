package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscribe")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    @PostMapping("/{followerId}")
    public void followUser(@PathVariable long followerId, @RequestParam long followeeId) throws DataFormatException {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataFormatException {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) throws DataFormatException {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) throws DataFormatException {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public int getFollowersCount(long followeeId) throws DataFormatException {
        return subscriptionService.getFollowersCount(followeeId);
    }

    public int getFollowingCount(long followerId) throws DataFormatException {
        return subscriptionService.getFollowingCount(followerId);
    }
}