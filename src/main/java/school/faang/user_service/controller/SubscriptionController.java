package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Tag(name = "Подписки", description = "Пользователи могут подписываться друг на друга")
@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Подписаться на пользователя",
            description = "Позволяет пользователю с id={followeeId} подписаться на пользователя " +
                    "с id={{followerId}}"
    )
    @PostMapping("/{followerId}")
    public void followUser(@PathVariable @Positive(message = "Id should be positive")
                           long followerId,
                           @PathVariable("userId") @Positive(message = "Id should be positive")
                           long followeeId) {

        validateSelfFollowing(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @Operation(summary = "Отписаться от пользователя",
            description = "Позволяет пользователю с id={followeeId} отписаться от пользователя " +
                    "с id={{followerId}}"
    )
    @DeleteMapping("/{followerId}")
    public void unfollowUser(@PathVariable @Positive long followerId, @PathVariable("userId") @Positive long followeeId) {

        validateSelfFollowing(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @Operation(summary = "Получить всех подписчиков пользователя",
            description = "Позволяет получить всех подписчиков пользователя с id={followeeId}, " +
                    "согласно переданному фильтру"
    )
    @GetMapping
    public List<UserDto> getFollowers(@PathVariable("userId") @Positive long followeeId, @RequestBody(required = false) UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Получить количество всех подписчиков пользователя",
            description = "Позволяет получить количество всех подписчиков пользователя с id={followeeId}"
    )
    @GetMapping("/count")
    public int getFollowersCount(@PathVariable("userId") @Positive long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Operation(summary = "Получить количество всех подписок пользователя",
            description = "Позволяет получить количество всех подписок пользователя с " +
                    "id={followeeId} (количество всех пользователей, на которых подписался " +
                    "данный пользователь)"
    )
    @GetMapping("/count-followees")
    public int getFolloweesCount(@PathVariable("userId") @Positive long followerId) {
        return subscriptionService.getFolloweesCount(followerId);
    }

    private void validateSelfFollowing(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(MessageError.SELF_FOLLOWING);
        }
    }
}
