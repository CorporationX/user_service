package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.service.subscription.SubscriptionService;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Tag(name = "Управление подписками")
@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    private static final String UNSUBSCRIBE_YOURSELF_EXCEPTION = "You can't unsubscribe from yourself.";
    private static final String SUBSCRIBE_YOURSELF_EXCEPTION = "You can't subscribe to yourself.";

    @Operation(summary = "Подписаться на пользователя по идентификатору")
    @PutMapping("/follow/{id}")
    public void followUser(@RequestParam("followerId") long followerId,
                           @PathVariable("id") long followeeId) {
        validationSameUser(followerId, followeeId, SUBSCRIBE_YOURSELF_EXCEPTION);
        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(summary = "Отписаться от пользователя по идентификатору")
    @DeleteMapping("/unfollow/{id}")
    public void unfollowUser(@RequestParam("followerId") long followerId,
                             @PathVariable("id") long followeeId) {
        validationSameUser(followerId, followeeId, UNSUBSCRIBE_YOURSELF_EXCEPTION);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(summary = "Получить количество подписчиков для пользователя по идентификатору")
    @GetMapping("/user/{id}/followers/count")
    public int getFollowersCount(@PathVariable("id") long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Получить количество подписок для пользователя по идентификатору")
    @GetMapping("/user/{id}/followees/count")
    public int getFollowingCount(@PathVariable("id") long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }

    @Operation(summary = "Получить список подписчиков для пользователя по идентификатору")
    @PostMapping("/user/{id}/followers")
    public List<UserDto> getFollowers(@PathVariable("id") long followeeId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Получить список подписок для пользователя по идентификатору")
    @PostMapping("/user/{id}/followees")
    public List<UserDto> getFollowing(@PathVariable("id") long followerId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    private void validationSameUser(long followerId, long followeeId, String message) {
        if (followerId == followeeId) {
            throw new DataValidationException(message);
        }
    }
}
