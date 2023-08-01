package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.SubscriptionDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.util.validator.SubscriptionValidator;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
@Tag(name = "Subscriptions", description = "Subscriptions management API")
public class SubscriptionController {
    private final SubscriptionService service;
    private final SubscriptionValidator validator;

    @PostMapping("/follow")
    @Operation(
            summary = "Follow a user",
            description = "Follows a user based on the provided subscription data.",
            tags = {"follow"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followed"),
            @ApiResponse(responseCode = "400", description = "Bad request: Invalid input or business rule violation"),
    })
    public ResponseEntity<?> followUser(@RequestBody SubscriptionDto dto) {
        validator.validateId(dto.getFollowerId(), dto.getFolloweeId());
        service.followUser(dto.getFollowerId(), dto.getFolloweeId());
        return ResponseEntity.ok().body("Followed");
    }

    @DeleteMapping("/unfollow")
    @Operation(
            summary = "Unfollow a user",
            description = "Unfollows a user based on the provided subscription data.",
            tags = {"unfollow"})
    public ResponseEntity<?> unfollowUser(@RequestBody SubscriptionDto dto) {
        validator.validateId(dto.getFollowerId(), dto.getFolloweeId());
        service.unfollowUser(dto.getFollowerId(), dto.getFolloweeId());
        return ResponseEntity.ok().body("Unfollowed");
    }

    @PostMapping(value = "/following/{followeeId}")
    @Operation(
            summary = "Get users followed by a user",
            description = "Retrieves users followed by a user based on the provided followee ID.",
            tags = {"following"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = {@Content(mediaType = "application/json")}
            )})
    public ResponseEntity<?> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter) {
        validator.validateId(followeeId);
        validator.validateFilterDto(filter);
        return ResponseEntity.ok(service.getFollowers(followeeId, filter));
    }

    @GetMapping("/followers/count/{followerId}")
    @Operation(
            summary = "Get followers count",
            description = "Retrieves the count of followers for the given follower ID.",
            tags = {"followers"})
    public ResponseEntity<?> getFollowersCount(@PathVariable long followerId) {
        validator.validateId(followerId);
        long followersCount = service.getFollowersCount(followerId);
        return ResponseEntity.ok(followersCount);
    }

    @PostMapping(value = "/followers/{followeeId}")
    @Operation(
            summary = "Get followers of a user",
            description = "Retrieves followers of a user based on the provided followee ID.",
            tags = {"followers"})
    public ResponseEntity<?> getFollowing(@PathVariable long followeeId, @RequestBody(required = false) UserFilterDto filter) {
        validator.validateId(followeeId);
        if (filter == null) filter = new UserFilterDto();
        validator.validateFilterDto(filter);
        List<UserDto> following = service.getFollowing(followeeId, filter);
        return ResponseEntity.ok().body(following);
    }

    @GetMapping("/following/count/{followerId}")
    @Operation(
            summary = "Get following count",
            description = "Retrieves the count of users followed by the given follower ID.",
            tags = {"following"})
    public ResponseEntity<Integer> getFollowingCount(@PathVariable long followerId) {
        validator.validateId(followerId);
        int followingCount = service.getFollowingCount(followerId);
        return ResponseEntity.ok().body(followingCount);
    }
}
