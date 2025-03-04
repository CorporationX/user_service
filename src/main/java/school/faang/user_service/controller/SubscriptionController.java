package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        log.info("Fetching following users for followerId: {}", followerId);
        List<UserDto> following = subscriptionService.getFollowing(followerId, userFilterDto);
        log.info("Found {} following users for followerId: {}", following.size(), followerId);
        return following;
    }

    public int getFollowingCount(long followerId) {
        log.info("Fetching following count for followerId: {}", followerId);
        int count = subscriptionService.getFollowingCount(followerId);
        log.info("FollowerId: {} follows {} users", followerId, count);
        return count;
    }
}
