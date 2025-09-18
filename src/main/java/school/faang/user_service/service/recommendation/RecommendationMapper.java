package school.faang.user_service.service.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Component
public class RecommendationMapper {
    public RecommendationDto toRecommendationDto(Recommendation recommendation) {
        if (recommendation == null) {
            return null;
        }

        return new RecommendationDto(recommendation.getId(),
                recommendation.getAuthor().getId(),
                recommendation.getReceiver().getId(),
                recommendation.getContent(),
                recommendation.getCreatedAt());
    }
}
