package school.faang.user_service.mapper;


import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillId", qualifiedByName = "map")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequests);

    @Named("map")
    default List<Long> map(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(SkillRequest::getId)
                .toList();
    }
}