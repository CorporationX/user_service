package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    @Mapping(target = "skillsId", source = "skills", qualifiedByName = "skillsToIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);
    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequestDto);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<SkillRequest> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream()
                .map(SkillRequest::getId)
                .collect(Collectors.toList());
    }
}