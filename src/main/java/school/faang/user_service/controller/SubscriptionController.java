package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.SUBSCRIPTION)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(ApiPath.FOLLOW)
    public ResponseEntity<String> followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        try {
            if (followerId == followeeId) {
                throw new DataValidationException(ExceptionMessages.SELF_SUBSCRIPTION);
            }
            boolean success = subscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.ok(success ? "Followed successfully" : "Failed to follow");
        } catch (DataValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping (ApiPath.UNFOLLOW)
    public ResponseEntity<String> unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        try {
            if (followerId == followeeId) {
                throw new DataValidationException(ExceptionMessages.SELF_UNSUBSCRIPTION);
            }
            boolean success = subscriptionService.unfollowUser(followerId, followeeId);
            return ResponseEntity.ok(success ? "Unfollowed successfully" : "Failed to unfollow");
        } catch (DataValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(ApiPath.FOLLOWERS)
    public ResponseEntity<List<UserSubscriptionDto>> getFollowers(@RequestParam long followeeId, @RequestBody UserSubscriptionFilterDto filter) {
        List<UserSubscriptionDto> followers = subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(followers);
    }

    @GetMapping(ApiPath.FOLLOWERS_COUNT)
    public ResponseEntity<Long> getFollowersCount(@RequestParam long followerId) {
        long count = subscriptionService.getFollowersCount(followerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping(ApiPath.FOLLOWING)
    public ResponseEntity<List<UserSubscriptionDto>> getFollowing(@RequestParam long followerId, @RequestBody UserSubscriptionFilterDto filter) {
        List<UserSubscriptionDto> following = subscriptionService.getFollowing(followerId, filter);
        return ResponseEntity.ok(following);
    }

    @GetMapping(ApiPath.FOLLOWING_COUNT)
    public ResponseEntity<Long> getFollowingCount(@RequestParam long followerId) {
        long count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }
}
