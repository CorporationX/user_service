package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Tag(name = "Подписки", description = "Операции по работе с подписками")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "Подписаться на пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная подписка"),
            @ApiResponse(responseCode = "404", description = "Невалидные данные")
    })
    @PostMapping("/follow")
    public void followUser(@RequestParam Long followerId, @RequestParam Long followeeId) throws DataValidationException {
            subscriptionService.followUser(followerId, followeeId);
            log.info("Follower {} followed user with id {}.", followerId, followeeId);
    }

    @Operation(summary = "Отписаться на пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная отписка"),
            @ApiResponse(responseCode = "404", description = "Невалидные данные")
    })
    @DeleteMapping("/unfollow")
    public void unfollowUser(@RequestParam Long followerId, @RequestParam Long followeeId) throws DataValidationException {
            subscriptionService.unfollowUser(followerId, followeeId);
            log.info("Follower {} unfollowed user with id {}.", followerId, followeeId);
    }

    @Operation(summary = "Получить подписчиков пользователя")
    @ApiResponse(responseCode = "200", content =
    @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class)
    ))
    @GetMapping("followers")
    public List<UserDto> getFollowers(
            @RequestParam Long followeeId,
            @RequestBody
            @Schema(description = "Фильтр пользователей", implementation = UserFilterDto.class)
            UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @Operation(summary = "Получить тех, на кого подписан пользователь")
    @ApiResponse(responseCode = "200", content =
    @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class)
    ))
    @GetMapping("following")
    public List<UserDto> getFollowing(
            @RequestParam Long followeeId,
            @RequestBody
            @Schema(description = "Фильтр пользователей", implementation = UserFilterDto.class)
            UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @Operation(summary = "Получить количество подписчиков пользователя")
    @ApiResponse(responseCode = "200")
    @GetMapping("followers/count/{followerId}")
    public int getFollowersCount(
            @PathVariable
            @Parameter(name = "id",
                    description = "Идентификатор пользователя",
                    required = true)
            Long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @Operation(summary = "Получить количество тех, на кого подписан пользователь")
    @ApiResponse(responseCode = "200")
    @GetMapping("following/count/{followerId}")
    public int getFollowingCount(
            @PathVariable
            @Parameter(name = "id",
                    description = "Идентификатор пользователя",
                    required = true)
            Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
