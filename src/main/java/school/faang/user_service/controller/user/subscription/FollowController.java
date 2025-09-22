package school.faang.user_service.controller.user.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.follower.FollowersPage;
import school.faang.user_service.service.user.subscription.FollowReadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class FollowController {
    private final FollowReadService followReadService;

    @GetMapping("/{authorId}/followers/ids")
    public FollowersPage getFollowerIds(
            @PathVariable long authorId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "1000") int limit
    ) {
        return followReadService.getFollowers(authorId, cursor, limit);
    }

    @GetMapping("/{userId}/following/ids")
    public FollowersPage getFollowingIds(@PathVariable long userId,
                                         @RequestParam(required = false) String cursor,
                                         @RequestParam(defaultValue = "1000") int limit) {
        return followReadService.getFollowing(userId, cursor, limit);
    }
}