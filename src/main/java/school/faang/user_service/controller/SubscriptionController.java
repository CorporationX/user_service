package school.faang.user_service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/{followerId}/follow/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void follow(@PathVariable Long followerId,
                       @PathVariable Long followeeId) {
        if (!Objects.equals(followerId, followeeId)) {
            subscriptionService.follow(followerId, followeeId);
        } else {
            log.info("Method follow, incorrect enter");
            throw new DataValidationException("You cannot subscribe to yourself");
        }
    }

    @PostMapping(value = "/{followerId}/unfollow/{followeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void unfollow(@PathVariable Long followerId,
                         @PathVariable Long followeeId) {
        if (!Objects.equals(followerId, followeeId)) {
            subscriptionService.unfollow(followerId, followeeId);
        } else {
            log.info("Method unfollow, incorrect enter");
            throw new DataValidationException("You choose yourself");
        }
    }

    @GetMapping(value = "/get/{userId}/followers")
    public List<UserDto> getFollowers(@PathVariable Long userId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowers(userId, filter);
    }

    @GetMapping(value = "/get/{userId}/following")
    public List<UserDto> getFollowing(@PathVariable Long userId,
                                      @RequestBody UserFilterDto filter) {
        return subscriptionService.getFollowing(userId, filter);
    }

    @GetMapping(value = "/{userId}/followers/count")
    public Long getFollowersCount(@PathVariable Long userId) {
        return subscriptionService.getFollowersCount(userId);
    }

    @GetMapping(value = "/get/{userId}/following/count")
    public Long getFollowingCount(@PathVariable Long userId) {
        return subscriptionService.getFollowingCount(userId);
    }

}
