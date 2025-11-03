package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface RecommendationRequestMapper {

    @Mapping(target = "receiver", ignore = true)
    RecommendationRequest toRecommendationRequest(CreateRecommendationRequestDto dto);

    RecommendationRequestDto toRecommendationRequestDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequestDto> toRecommendationRequestListDto(List<RecommendationRequest> recommendationRequest);

}
