package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "status", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);
}
