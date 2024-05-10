package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidatorImpl implements SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void validateSubscriptionExistence(SubscriptionRequestDto subscriptionRequestDto) {
        boolean isAlreadyExists = subscriptionRepository.existsByFollowerIdAndFolloweeId(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
        if (isAlreadyExists) {
            throw new DataValidationException("Subscription already exists");
        }
    }

    @Override
    public void validateFollowerAndFolloweeIds(SubscriptionRequestDto subscriptionRequestDto) {
        if (subscriptionRequestDto.getFollowerId() == subscriptionRequestDto.getFolloweeId()) {
            throw new DataValidationException("Follower and followee has same ids");
        }
    }
}
