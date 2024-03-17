package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Controller
@AllArgsConstructor
public class RecommendationController {
    private RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateContent(recommendation.getContent());
        return recommendationService.create(recommendation);
    }

    /*

    Создайте метод giveRecommendation(RecommendationDto recommendation) в классе
    RecommendationController.
    Этот метод будет обрабатывать запрос и предоставлять количество подписчиков.
    Здесь recommendation - рекомендацию, которую дали пользователю.

     */


    private void validateContent(String content) throws DataValidationException {
        if (content != null && content.isBlank()) {
            throw new DataValidationException(
                    "\"" + content + "\" is unavailable value for this field!");
        }
    }
}