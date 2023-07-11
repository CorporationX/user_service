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
        validateFollower(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }
    private boolean validateFollower(long followerId, long followeeId){
        if(followerId == followeeId){
            throw new DataValidationException("Пользователь пытается подписаться сам на себя");
        }
        if(followerId <= 0 || followeeId <= 0){
            throw new IllegalArgumentException("Пользователя с отрицательным Id не может быть");
        }
        return true;
    }
}
