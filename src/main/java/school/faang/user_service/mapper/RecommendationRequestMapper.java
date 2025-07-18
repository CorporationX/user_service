package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface RecommendationRequestMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    @Mapping(source = "requester", target = "requester")
    @Mapping(source = "receiver", target = "receiver")
    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);
}
