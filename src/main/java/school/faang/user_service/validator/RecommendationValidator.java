package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class RecommendationValidator {
    private final int MIN_COUNT_MONTH = 6;
    private final String CONTENT_IS_BLANK = "Content is blank";
    private final String CONTENT_IS_NULL = "Content is null";
    private final String ID_MESSAGE = "Id must be greater than 0";
    private String DATA_VALIDATION_MESSAGE =
            "This author has already made a recommendation. Less than 6 months have passed since the last recommendation.";

    public void validateRecommendationDto(RecommendationDto recommendation) {
        validateId(recommendation.getAuthorId());
        validateId(recommendation.getReceiverId());
        validateContent(recommendation.getContent());
    }

    public void validateRecommendationDate(Optional<Recommendation> recommendation) {
        if (recommendation.isPresent()) {
            LocalDateTime targetDate = LocalDateTime.now().minusMonths(MIN_COUNT_MONTH);
            LocalDateTime createRecommendation = recommendation.get().getCreatedAt();
            if (targetDate.isBefore(createRecommendation)) {
                log.error(DATA_VALIDATION_MESSAGE);
                throw new DataValidationException(DATA_VALIDATION_MESSAGE);
            }
        }
    }

    private void validateContent(String content) {
        if (content == null) {
            log.error(CONTENT_IS_NULL);
            throw new DataValidationException(CONTENT_IS_NULL);
        }
        if (content.isBlank()) {
            log.error(CONTENT_IS_BLANK);
            throw new DataValidationException(CONTENT_IS_BLANK);
        }

    }

   public void validateId(long id) {
        if (id <= 0) {
            log.error(ID_MESSAGE);
            throw new DataValidationException(ID_MESSAGE);
        }
    }
}
