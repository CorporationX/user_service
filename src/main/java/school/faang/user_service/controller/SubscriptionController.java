package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
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
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/followers/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowers(@PathVariable long followeeId, UserFilterDto filter) {
        return userMapper.toDto(subscriptionService.getFollowers(followeeId, filter));
    }


    @GetMapping("/following/{followerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getFollowing(@PathVariable long followerId, UserFilterDto filter) {
        return userMapper.toDto(subscriptionService.getFollowing(followerId, filter));
    }

    @GetMapping("/followersCount/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public Integer getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followingCount/{followerId}")
    @ResponseStatus(HttpStatus.OK)
    public Integer getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
