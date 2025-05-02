package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Tag(name = "subscription_methods")
@RequiredArgsConstructor
@Controller
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Получить подписчиков пользователя",
            description = "Возвращает список DTO пользователей, которые подписаны на указанного пользователя, с учетом заданного фильтра."
    )
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        log.info("Getting followers for followeeId: {} with filter: {}", followeeId, userFilterDto);
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, userFilterDto);
        log.info("Retrieved {} followers for followeeId: {}", followers.size(), followeeId);
        return followers;
    }

    @Operation(
            summary = "Получить количество подписчиков пользователя",
            description = "Возвращает общее количество пользователей, подписанных на указанного пользователя."
    )
    public int getFollowersCount(long followeeId) {
        log.info("Getting followers count for followeeId: {}", followeeId);
        int followersCount = subscriptionService.getFollowersCount(followeeId);
        log.info("Retrieved followers count: {} for followeeId: {}", followersCount, followeeId);
        return followersCount;
    }

    @Operation(
            summary = "Подписаться на пользователя",
            description = "Позволяет одному пользователю подписаться на другого пользователя."
    )
    public void followUser(long followerId, long followeeId) {
        log.info("Received request to follow user. FollowerId: {}, FolloweeId: {}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        log.info("User {} successfully followed user {}", followerId, followeeId);
    }

    @Operation(
            summary = "Отписаться от пользователя",
            description = "Позволяет пользователю отменить подписку на другого пользователя."
    )
    public void unfollowUser(long followerId, long followeeId) {
        log.info("Unfollow request received: Follower ID = {}, Followee ID = {}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        log.info("Successfully unfollowed: Follower ID = {}, Followee ID = {}", followerId, followeeId);
    }

    @Operation(
            summary = "Получить список подписок пользователя",
            description = "Возвращает список DTO пользователей, на которых подписан указанный пользователь, с учетом заданного фильтра."
    )
    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        log.info("Fetching following users for followerId: {}", followerId);
        List<UserDto> following = subscriptionService.getFollowing(followerId, userFilterDto);
        log.info("Found {} following users for followerId: {}", following.size(), followerId);
        return following;
    }

    @Operation(
            summary = "Получить количество подписок пользователя",
            description = "Возвращает общее количество пользователей, на которых подписан указанный пользователь."
    )
    public int getFollowingCount(long followerId) {
        log.info("Fetching following count for followerId: {}", followerId);
        int count = subscriptionService.getFollowingCount(followerId);
        log.info("FollowerId: {} follows {} users", followerId, count);
        return count;
    }
}
