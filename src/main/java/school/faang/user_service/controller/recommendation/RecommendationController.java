package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.validator.RequestValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        RequestValidator.validateStringNotEmpty(recommendationDto.content(), "content");
        RequestValidator.validateNotNull(recommendationDto.receiverId(), "receiverId");
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        RequestValidator.validateStringNotEmpty(recommendationDto.content(), "content");
        return recommendationService.update(recommendationId, recommendationDto);
    }

    public void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }

}
