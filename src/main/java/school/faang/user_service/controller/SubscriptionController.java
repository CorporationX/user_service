package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Can't follow yourself");
        }
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId != followeeId) {
            subscriptionService.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Can't unfollow yourself");
        }
    }

    @GetMapping("/followers/{followeeId}")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable long followeeId, UserFilterDto filter) {
        List<UserDto> followers = userMapper.toDto(subscriptionService.getFollowers(followeeId, filter));
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{followerId}")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable long followerId, UserFilterDto filter) {
        List<UserDto> following = userMapper.toDto(subscriptionService.getFollowing(followerId, filter));
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followersCount/{followeeId}")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable long followeeId) {
        int count = subscriptionService.getFollowersCount(followeeId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/followingCount/{followerId}")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable long followerId) {
        int count = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(count);
    }
}
