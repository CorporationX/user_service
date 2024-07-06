package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/subscripts")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;


    @RequestMapping(value = "/followUser",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> followUser(@RequestParam(value = "followerId") Long followerId,
                                             @RequestParam(value = "followeeId") Long followeeId) {
        if (!Objects.equals(followerId, followeeId)) {
            subscriptionService.followUser(followerId, followeeId);
            return ResponseEntity.status(HttpStatus.OK).body("Subscribe active");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot subscribe to yourself");
        }
    }

    @RequestMapping(value = "/unFollowUser",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unfollowUser(@RequestParam(value = "followerId") Long followerId,
                                               @RequestParam(value = "followeeId") Long followeeId) {
        if (!Objects.equals(followerId, followeeId)) {
            subscriptionService.unFollowUser(followerId, followeeId);
            return ResponseEntity.status(HttpStatus.OK).body("Subscribe deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You choose yourself");
        }
    }

    @RequestMapping(value = "/getFollowers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getFollowers(@RequestParam(value = "followerId") Long followerId,
                                                      @RequestBody UserFilterDto filter) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.getFollowers(followerId, filter));
    }

    @RequestMapping(value = "/getFollowing",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getFollowing(@RequestParam(value = "followeeId") Long followeeId,
                                                      @RequestBody UserFilterDto filter) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.getFollowing(followeeId, filter));
    }


    @RequestMapping(value = "/getFollowersCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getFollowersCount(@RequestParam(value = "followerId") Long followerId) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.getFollowersCount(followerId));
    }


    @RequestMapping(value = "/getFollowingCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getFollowingCount(@RequestParam(value = "followerId") Long followerId) {
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.getFollowingCount(followerId));
    }


}
