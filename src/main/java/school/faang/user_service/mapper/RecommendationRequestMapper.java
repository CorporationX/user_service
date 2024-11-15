package school.faang.user_service.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Named("toDto")
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "skillRequestToSkillIdsMapper")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Named("toEntity")
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("skillRequestToSkillIdsMapper")
    default List<Long> skillRequestToSkillIdsMapper(List<SkillRequest> skills) {
        return skills.stream().map(SkillRequest::getId).toList();
    }

    @Named("toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequests);

    @Named("toEntityList")
    @IterableMapping(qualifiedByName = "toEntity")
    List<RecommendationRequest> toEntityList(List<RecommendationRequestDto> recommendationRequestDtos);
}
