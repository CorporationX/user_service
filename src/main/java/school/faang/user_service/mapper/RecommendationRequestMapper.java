package school.faang.user_service.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.recommendation.RecommendationRequest;
import school.faang.user_service.model.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skills", target = "skillIds", qualifiedByName = "skillRequestsToSkillsId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    List<RecommendationRequest> toEntityList(List<RecommendationRequestDto> recommendationRequestDtoList);

    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequestList);

    @Named("skillRequestsToSkillsId")
    default List<Long> skillRequestsToSkillsId(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return null;
        }
        return skillRequests.stream()
                .map(skillRequest -> skillRequest.getSkill().getId())
                .toList();
    }
}
