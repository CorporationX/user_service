package school.faang.user_service.validator;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;

@Component
@NoArgsConstructor
public class RecommendationValidator {
    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent() == null || recommendationDto.getContent().trim().isEmpty()) {
            throw new DataValidationException("Validation failed. The text cannot be empty.");
        }
    }

    public void validateId(long id) {
        if (id <= 0) {
            throw new DataValidationException("Id is not correct.");
        }
    }

    public void validateUserAndAuthorIds(long userId, long authorId) {
        if (userId != authorId) {
            throw new DataValidationException("Only the author can update the recommendation.");
        }
    }
}
