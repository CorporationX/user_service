package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
@Tag(name = "Подписки", description = "Подписки и подписчики пользователя")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;


    @Operation(summary = "Подписаться на пользователя")
    @PostMapping("/{followerId}/followers/{followeeId}")
    public void followUser(
            @PathVariable
            @Parameter(description = "Идентификатор пользователя", example = "1")
            long followerId,
            @PathVariable
            @Parameter(description = "Идентификатор подписчика", example = "1")
            long followeeId
    ) {
        validate(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(summary = "Отписаться от пользователя")
    @DeleteMapping("/{followerId}/followers/{followeeId}")
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        validate(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(summary = "Получить подписчиков")
    @PostMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Получить подписки")
    @PostMapping("/{followerId}/following")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @Operation(summary = "Получить количество подписчиков")
    @GetMapping("/{followeeId}/followers/count")
    public long getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Получить количество подписок")
    @GetMapping("/{followerId}/following/count")
    public long getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    private void validate(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and folowee can not be the same");
        }
    }
}