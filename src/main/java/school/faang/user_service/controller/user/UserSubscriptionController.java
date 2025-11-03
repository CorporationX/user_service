package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;
import java.util.List;

@RequiredArgsConstructor
@Component
@RequestMapping("/subscriptions")
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;
    private final UserContext userContext;

    @PostMapping("/{followeeId}/follow")
    public void followUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("/{followeeId}/unfollow")
    public void unfollowUser(@PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        userSubscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/{followeeId}/followers/count")
    public CountResponse getFollowersCount(@PathVariable long followeeId) {
        return userSubscriptionService.getFollowersCount(followeeId);
    }

    @GetMapping("/{followerId}/followees/count")
    public CountResponse getFolloweesCount(@PathVariable long followerId) {
        return userSubscriptionService.getFolloweesCount(followerId);
    }

    @GetMapping("/{followeeId}/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @ModelAttribute UserFiltersDto filters) {
        return userSubscriptionService.getFollowers(followeeId, filters);
    }

    @GetMapping("/{followerId}/followees")
    public List<UserDto> getFollowees(@PathVariable long followerId, @ModelAttribute UserFiltersDto filters) {
        return userSubscriptionService.getFollowees(followerId, filters);
    }
}
