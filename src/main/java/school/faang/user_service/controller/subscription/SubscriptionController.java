package school.faang.user_service.controller.subscription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping("/follow")
  public ResponseEntity<String> followUser(
      @RequestParam @NotNull @Valid long followerId, @RequestParam @NotNull @Valid long followeeId)
      throws DataValidationException {
    if (followerId == followeeId) {
      throw new DataValidationException("Нельзя подписаться или отписаться от самого себя");
    }
    subscriptionService.followUser(followerId, followeeId);
    return ResponseEntity.ok("Success");
  }

  @PostMapping("/unfollow")
  public ResponseEntity<String> unfollowUser(
      @RequestParam @NotNull long followerId, @RequestParam @NotNull long followeeId)
      throws DataValidationException {
    if (followerId == followeeId) {
      throw new DataValidationException("Нельзя отписаться от самого себя");
    }
    subscriptionService.unfollowUser(followerId, followeeId);
    return ResponseEntity.ok("Success");
  }

  @GetMapping("/followers")
  public ResponseEntity<List<UserDto>> getFollowers(
      @RequestParam @NotNull long followeeId, @RequestBody(required = false) UserFilterDto filter) {
    List<UserDto> followers = subscriptionService.getFollowers(followeeId, filter);
    return ResponseEntity.ok(followers);
  }

  @GetMapping("/followers/count")
  public ResponseEntity<Integer> getFollowersCount(@RequestParam @NotNull long followeeId) {
    int followersCount = subscriptionService.getFollowersCount(followeeId);
    return ResponseEntity.ok(followersCount);
  }

  @GetMapping("/following")
  public ResponseEntity<List<UserDto>> getFollowing(
      @RequestParam @NotNull long followerId,
      @RequestParam(required = false) String namePattern,
      @RequestParam(required = false) String emailPattern,
      @RequestParam(required = false) String cityPattern) {
    UserFilterDto filter = new UserFilterDto();
    filter.setNamePattern(namePattern);
    filter.setEmailPattern(emailPattern);
    filter.setCityPattern(cityPattern);
    List<UserDto> following = subscriptionService.getFollowing(followerId, filter);
    return ResponseEntity.ok(following);
  }

  @GetMapping("/following/count")
  public ResponseEntity<Integer> getFollowingCount(@RequestParam @NotNull long followerId) {
    int followingCount = subscriptionService.getFollowingCount(followerId);
    return ResponseEntity.ok(followingCount);
  }
}
