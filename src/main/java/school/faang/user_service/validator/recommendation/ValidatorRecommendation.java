package school.faang.user_service.validator.recommendation;

import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public interface ValidatorRecommendation {
    void validateStatus(RequestStatus status);

    void validateRecommendationToRequest(Long id, Long receiverId, String paramName);

    void validateTimeOutSixMount(LocalDateTime created, String paramName);

    void validateRecommendationIsRequest(Long requesterId, Long receiverId, String paramName);
}
