package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.SubscriptionService;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        log.info("Received request to follow user. FollowerId: {}, FolloweeId: {}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        log.info("User {} successfully followed user {}", followerId, followeeId);

    public void unfollowUser(long followerId, long followeeId) {
        log.info("Unfollow request received: Follower ID = {}, Followee ID = {}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        log.info("Successfully unfollowed: Follower ID = {}, Followee ID = {}", followerId, followeeId);
    }
}
