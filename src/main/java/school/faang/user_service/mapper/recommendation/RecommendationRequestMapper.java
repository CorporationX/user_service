package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "requesterId", target = "requester.id")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);
}
