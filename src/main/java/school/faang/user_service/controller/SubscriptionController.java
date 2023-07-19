package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(Long followerId, Long followeeId) {
        validate(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId){
        validate(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public void validate(Long firstId, Long secondId) {
        if (firstId <= 0 || secondId <= 0){
            throw new DataValidationException("Id cannot be less 0! ");
        } else if (firstId == null || secondId == null){
            throw new DataValidationException("Id cannot be null !");
        }
    }
}
