package school.faang.user_service.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

@RequiredArgsConstructor
@Controller

public class RecommendationController {
    private RecommendationService recommendationService;

    public void giveRecommendation(RecommendationDto recommendation){
        validateRecommendation(recommendation);
        recommendationService.create(recommendation);
    }
    public void validateRecommendation(RecommendationDto recommendation){
        if (recommendation.getContent().isBlank()) { //по идее необходимость в recommendationDto.getContent() == null отсутствует, так как isBlank это проверяет?
            throw new DataValidationException();
        }
    }
}
