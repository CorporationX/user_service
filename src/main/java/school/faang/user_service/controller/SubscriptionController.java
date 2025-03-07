package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        log.info("Fetching following users for followerId: {}", followerId);
        List<UserDto> following = subscriptionService.getFollowing(followerId, userFilterDto);
        log.info("Found {} following users for followerId: {}", following.size(), followerId);
        return following;
    }

    public int getFollowingCount(long followerId) {
        log.info("Fetching following count for followerId: {}", followerId);
        int count = subscriptionService.getFollowingCount(followerId);
        log.info("FollowerId: {} follows {} users", followerId, count);
        return count;
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
