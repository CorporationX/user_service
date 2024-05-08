package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    RecommendationRequest fromDto(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "requester.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);
}
