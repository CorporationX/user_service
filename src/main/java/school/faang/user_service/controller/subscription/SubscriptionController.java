package school.faang.user_service.controller.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.follow.ProjectFollowerEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.publisher.RedisTopics;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final EventPublisher eventPublisher;

    @PostMapping("/follow/{followerId}/{followeeId}")
    public void followUser(@PathVariable long followerId,
                           @PathVariable long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписываться на самого себя.");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписываться от самого себя.");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        return subscriptionService.getFollowing(followeeId, filterDto);
    }

    @PostMapping("follow/project")
    public void projectFollowerEvent(@RequestBody ProjectFollowerEvent projectFollowerEvent){
        eventPublisher.publishToTopic(RedisTopics.PROJECT_FOLLOWER_CHANNEL.getTopicName(), projectFollowerEvent);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
