package school.faang.user_service.validator.recommendation;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

@Component
public class ValidatorRecommendationImpl implements ValidatorRecommendation {
    @Value("${date.time.out}")
    private int timeOutMonth;

    @Override
    public void validateStatus(RequestStatus status) {
        if (!status.equals(RequestStatus.PENDING)) {
            throw new DataValidationException("Status no pending!");
        }
    }

    @Override
    public void validateRecommendationToRequest(Long id, Long receiverId, String paramName) {
        if (!id.equals(receiverId)) {
            throw new DataValidationException(paramName + " invalid recipient!");
        }
    }

    @Override
    public void validateTimeOutSixMount(LocalDateTime created, String paramName) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime limitDate = currentDate.minusMonths(timeOutMonth);
        if (created.isAfter(limitDate)) {
            throw new DataValidationException(paramName
                    + " less than 6 months have passed since the last recommendation!");
        }
    }

    public void validateRecommendationIsRequest(Long requesterId, Long receiverId, String paramName) {
        if (requesterId.equals(receiverId)) {
            throw new DataValidationException(paramName + " cannot ask for a recommendation from himself!");
        }
    }
}
