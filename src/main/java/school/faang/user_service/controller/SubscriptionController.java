package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
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

    @PutMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @PutMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/followers/{followeeId}/filter")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        List<UserDto> userDtos = userMapper.toDto(subscriptionService.getFollowers(followeeId, filter));
        return userDtos;
    }


    @PostMapping("/following/{followerId}/filter")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        List<UserDto> userDtos = userMapper.toDto(subscriptionService.getFollowing(followerId, filter));
        return userDtos;
    }

    @GetMapping("/followersCount/{followeeId}")
    public Integer getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/followingCount/{followerId}")
    public Integer getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
