package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(source = "requesterId", target = "entity.id")
    @Mapping(source = "receiverId", target = "entity.id")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

    @InheritInverseConfiguration
    RecommendationRequest toRecommendationRequest(RecommendationRequestDto recommendationRequestDto);
}
