package school.faang.user_service.controller.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.ShortUserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription")
@AllArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow/{followerId}/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable @NotNull Long followerId, @PathVariable @NotNull Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("unfollow/{followerId}/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable @NotNull Long followerId, @PathVariable @NotNull Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("followers/{followeeId}")
    public ResponseEntity<List<ShortUserDto>> getFollowers(@PathVariable @NotNull Long followeeId, @RequestBody @NotNull UserFilterDto filter) {
        List<ShortUserDto> users = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(users);
    }

    @GetMapping("followers-count/{followerId}")
    public ResponseEntity<Long> getFollowersCount(@PathVariable @NotNull Long followerId) {
        long followersCount = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(followersCount);
    }

    @PostMapping("following/{followerId}")
    public ResponseEntity<List<ShortUserDto>> getFollowing(@PathVariable @NotNull Long followerId, UserFilterDto filter) {
        List<ShortUserDto> users = subscriptionService.getFollowing(followerId, filter);
        return ResponseEntity.ok(users);
    }

    @GetMapping("following-count/{followeeId}")
    public ResponseEntity<Long> getFollowingCount(@PathVariable @NotNull Long followeeId) {
        long followeeCount = subscriptionService.getFollowingCount(followeeId);
        return ResponseEntity.ok(followeeCount);
    }

    @ExceptionHandler(DataValidationException.class)
    private ResponseEntity<String> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @GetMapping("{userId}")
    public List<Long> getUserFollowers(@PathVariable long userId) {
        return subscriptionService.getFollowers(userId);
    }

}
