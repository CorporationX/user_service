package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.annotation.CustomExceptionHandler;
import school.faang.user_service.constant.SuccessMessages;
import school.faang.user_service.dto.ResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@CustomExceptionHandler
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<ResponseDto> followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(SuccessMessages.SUBSCRIBE_SUCCESS));
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<ResponseDto> unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.ok(new ResponseDto(SuccessMessages.UNSUBSCRIBE_SUCCESS));
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        List<User> users = subscriptionService.getFollowers(followeeId, filter);
        return userMapper.toDtos(users);
    }

    @GetMapping("/{followeeId}/followers/count")
    public int getFollowersCount(@PathVariable long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/following")
    public List<UserDto> getFollowing(@PathVariable long followerId, @RequestBody UserFilterDto filter) {
        List<User> users = subscriptionService.getFollowing(followerId, filter);
        return userMapper.toDtos(users);
    }

    @GetMapping("/{followerId}/following/count")
    public int getFollowingCount(@PathVariable long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }


}
