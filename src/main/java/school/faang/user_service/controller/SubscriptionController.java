package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public List<UserDto> getFollowing(long followerId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowing(followerId, userFilterDto);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}
