package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);
}
