package school.faang.user_service.util.recommendation;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
@UtilityClass
public class RecommendationValidatorUtil {
    public static void validate(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("Recommendation cannot be null");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("Author ID cannot be null");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("Receiver ID cannot be null");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation text cannot be empty");
        }
    }
}