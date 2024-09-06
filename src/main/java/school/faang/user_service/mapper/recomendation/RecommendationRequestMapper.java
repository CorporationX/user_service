package school.faang.user_service.mapper.recomendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "requester.id", target = "requesterId")
    RecommendationRequestDto mapToDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    RecommendationRequest mapToEntity(RecommendationRequestDto recommendationRequestDto);

}
