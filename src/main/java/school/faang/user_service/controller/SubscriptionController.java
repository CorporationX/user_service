package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        try {
            if (followerId == followeeId) {
                throw new DataValidationException("You cannot follow yourself!");
            }
            boolean success = subscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.ok(success ? "Followed successfully" : "Failed to follow");
        } catch (DataValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        try {
            if (followerId == followeeId) {
                throw new DataValidationException("You cannot unfollow yourself!");
            }
            boolean success = subscriptionService.unfollowUser(followerId, followeeId);
            return ResponseEntity.ok(success ? "Unfollowed successfully" : "Failed to unfollow");
        } catch (DataValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@RequestParam long followeeId, @RequestBody UserFilterDto filter) {
        List<UserDto> followers = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/followers/count")
    public ResponseEntity<Long> getFollowersCount(@RequestParam long followerId) {
        long count = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/following")
    public ResponseEntity<List<UserDto>> getFollowing(@RequestParam long followerId, @RequestBody UserFilterDto filter) {
        List<UserDto> following = subscriptionService.getFollowing(followerId, filter);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/following/count")
    public ResponseEntity<Long> getFollowingCount(@RequestParam long followerId) {
        long count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }
}
