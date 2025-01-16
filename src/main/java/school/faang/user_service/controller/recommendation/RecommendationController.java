package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation = recommendationService.create(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        recommendation = recommendationService.update(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationMapper.toRecommendationDtoList(
                recommendationService.getAllUserRecommendations(receiverId));
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationMapper.toRecommendationDtoList(
                recommendationService.getAllGivenRecommendations(authorId));
    }
}
