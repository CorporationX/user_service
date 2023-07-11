package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId){
        if(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)){
            throw new DataValidationException("Пользователь пытается подписаться сам на себя");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }
}
