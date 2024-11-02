package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto){
        return recommendationService.create(recommendationDto);
    }

    private void validation(RecommendationDto recommendationDto){
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()){
            throw new DataValidationException("Рекомендация обязательно должна содержать текст");
        }
    }
}
