package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;
    private static final String DEFAULT_MAX_EXPERIENCE = "2147483647";

    @PostMapping("/follow/{followeeId}")
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        log.info("The user {} subscribes to {}", followerId, followeeId);
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        log.info("The user {} unsubscribes from {}", followerId, followeeId);
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable long followeeId) {
        log.debug("Requesting the number of subscribers for a user: {}", followeeId);
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable long followerId) {
        log.debug("Requesting the number of subscriptions for a user: {}", followerId);
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId,
                                      @RequestParam(required = false) String namePattern,
                                      @RequestParam(required = false) String phonePattern,
                                      @RequestParam(defaultValue = "0") int experienceMin,
                                      @RequestParam(defaultValue = DEFAULT_MAX_EXPERIENCE) int experienceMax) {
        log.debug("Requesting a list of subscribers for a user: {}", followeeId);
        UserFiltersDto filters = new UserFiltersDto(namePattern, phonePattern, experienceMin, experienceMax);
        return userSubscriptionService.getFollowers(followeeId, filters);
    }

    @GetMapping("/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable long followerId,
                                      @RequestParam(required = false) String namePattern,
                                      @RequestParam(required = false) String phonePattern,
                                      @RequestParam(defaultValue = "0") int experienceMin,
                                      @RequestParam(defaultValue = DEFAULT_MAX_EXPERIENCE) int experienceMax) {
        log.debug("Requesting a list of subscriptions for a user: {}", followerId);
        UserFiltersDto filters = new UserFiltersDto(namePattern, phonePattern, experienceMin, experienceMax);
        return userSubscriptionService.getFollowees(followerId, filters);
    }
}
