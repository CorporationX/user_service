package school.faang.user_service.utils.validator;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

public class ValidatorService {
    public void validateTime(Recommendation recommendation){
        LocalDateTime recommendationCreate = recommendation.getCreatedAt();
        if(!recommendationCreate.isAfter(LocalDateTime.now().minusMonths(6))){
            throw new DataValidationException("Time has not expired");
        }
    }
}
