package school.faang.user_service.validator;

import school.faang.user_service.dto.subscription.SubscriptionRequestDto;

public interface SubscriptionValidator {

    void validateSubscriptionExistence(SubscriptionRequestDto subscriptionRequestDto);

    void validateFollowerAndFolloweeIds(SubscriptionRequestDto subscriptionRequestDto);
}
