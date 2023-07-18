package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    public void followUser(long followerId, long followeeId) {
        if (repository.existsByFollowerIdAndFolloweeId(followerId,followeeId)){
            throw new DataValidationException("User has already followed");
        }
        repository.followUser(followerId,followeeId);
    }
}
