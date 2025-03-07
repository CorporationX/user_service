package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        log.info("Getting followers for followeeId: {} with filter: {}", followeeId, userFilterDto);
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, userFilterDto);
        log.info("Retrieved {} followers for followeeId: {}", followers.size(), followeeId);
        return followers;
    }

    public int getFollowersCount(long followeeId) {
        log.info("Getting followers count for followeeId: {}", followeeId);
        int followersCount = subscriptionService.getFollowersCount(followeeId);
        log.info("Retrieved followers count: {} for followeeId: {}", followersCount, followeeId);
        return followersCount;
    }

    public void followUser(long followerId, long followeeId) {
        log.info("Received request to follow user. FollowerId: {}, FolloweeId: {}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        log.info("User {} successfully followed user {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        log.info("Unfollow request received: Follower ID = {}, Followee ID = {}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        log.info("Successfully unfollowed: Follower ID = {}, Followee ID = {}", followerId, followeeId);
    }


}
