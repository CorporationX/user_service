package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.annotation.AppExceptionHandler;
import school.faang.user_service.dto.user.UserAmountDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@AppExceptionHandler
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @PostMapping("/{followerId}/follow/{followeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public void unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @PostMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserExtendedFilterDto filter) {
        List<User> followers = subscriptionService.getFollowers(followeeId, filter);
        return userMapper.toDtos(followers);
    }

    @GetMapping("/{followeeId}/followers/count")
    public UserAmountDto getFollowersCount(@PathVariable long followeeId) {
        UserAmountDto userAmountDto = new UserAmountDto();
        userAmountDto.setUserAmount(subscriptionService.getFollowersCount(followeeId));
        return userAmountDto;
    }

    @PostMapping("/{followerId}/following")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserExtendedFilterDto filter) {
        List<User> following = subscriptionService.getFollowing(followerId, filter);
        return userMapper.toDtos(following);
    }

    @GetMapping("/{followerId}/following/count")
    public UserAmountDto getFollowingCount(@PathVariable long followerId) {
        UserAmountDto userAmountDto = new UserAmountDto();
        userAmountDto.setUserAmount(subscriptionService.getFollowingCount(followerId));
        return userAmountDto;
    }
}
