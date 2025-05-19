package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/following/{followeeId}")
    public void follow(@PathVariable(name = "userId")
                       @NotNull(message = "Follower ID must be provided")
                       @Positive(message = "Follower ID must be positive number")
                       Long followerId,
                       @PathVariable
                       @NotNull(message = "Followee ID must be provided")
                       @Positive(message = "Followee ID must be positive number")
                       Long followeeId) {
        log.info("Incoming follow request: followerId={} -> followeeId={}", followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/following/{followeeId}")
    public void unfollowUser(@PathVariable(name = "userId")
                             @NotNull(message = "Follower ID must be provided")
                             @Positive(message = "Follower ID must be positive number")
                             Long followerId,
                             @PathVariable
                             @NotNull(message = "Followee ID must be provided")
                             @Positive(message = "Followee ID must be positive number")
                             Long followeeId) {
        log.info("Incoming unfollow request: followerId={} -> followeeId={}", followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@PathVariable(name = "userId")
                                      @NotNull(message = "Followee ID must be provided")
                                      @Positive(message = "Followee ID must be positive number")
                                      Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} followers with filter {}", followeeId, filter);
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/followers/count")
    public int getFollowersCount(@PathVariable(name = "userId")
                                 @NotNull(message = "Followee ID must be provided")
                                 @Positive(message = "Followee ID must be positive number")
                                 Long followeeId) {
        log.info("Received GET {} followers count request", followeeId);
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/following")
    public List<UserDto> getFollowing(@PathVariable(name = "userId")
                                      @NotNull(message = "Followee ID must be provided")
                                      @Positive(message = "Followee ID must be positive number")
                                      Long followeeId,
                                      @Valid @ModelAttribute UserFilterDto filter) {
        log.info("Received GET {} following with filter {}", followeeId, filter);
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("/following/count")
    public int getFollowingCount(@PathVariable(name = "userId")
                                 @NotNull(message = "Followee ID must be provided")
                                 @Positive(message = "Followee ID must be positive number")
                                 Long followeeId) {
        log.info("Received GET {} following count request", followeeId);
        return subscriptionService.getFollowingCount(followeeId);
    }
}
