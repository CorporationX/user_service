package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public void followUser(long followerId, long followeeId) {
        boolean isExist = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isExist) {
            throw new DataValidationException("User " + followerId +
                    " subscription to user " + followeeId + " exist");
        }
        repository.followUser(followerId, followeeId);
    }
}
