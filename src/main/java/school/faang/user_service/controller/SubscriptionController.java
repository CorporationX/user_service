package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

@Tag(name = "Контролер подписок")
@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionValidator subscriptionValidator;
    private final UserContext userContext;

    @Operation(
            summary = "Подписка",
            description = "Позволяет подписаться на пользователя",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "id пользователя", required = true)})
    @PutMapping("/follow/{id}")
    public void followUser(@PathVariable("id") long followeeId) {
        long followerId = userContext.getUserId();
        subscriptionValidator.validateUserIds(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(
            summary = "Отписка",
            description = "Позволяет отписаться от пользователя",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "id пользователя", required = true)}
    )
    @DeleteMapping("/unfollow/{id}")
    public void unfollowUser(@PathVariable("id") long followeeId) {
        long followerId = userContext.getUserId();
        subscriptionValidator.validateUserIds(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(summary = "Получение подписчиков")
    @PostMapping("/user/{id}/followers")
    public List<UserDto> getFollowers(@PathVariable("id") long followeeId, @RequestBody @Valid UserFilterDto filters) {
        return subscriptionService.getFollowers(followeeId, filters);
    }

    @Operation(summary = "Получение подписок")
    @PostMapping("/user/{id}/followees")
    public List<UserDto> getFollowing(@PathVariable("id") long followerId, @RequestBody @Valid UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    @Operation(summary = "Получение количества подписчиков")
    @GetMapping("/user/{id}/followers/count")
    public long getFollowersCount(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Получение количества подписок")
    @GetMapping("/user/{id}/followees/count")
    public long getFollowingCount(@PathVariable("id") long followerId){
        return subscriptionService.getFollowingCount(followerId);
    }

    @Operation(summary = "Получение ID подписчиков")
    @PostMapping("/user/{id}/followersIds")
    public List<Long> getFollowers(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowerIds(followeeId);
    }
}
