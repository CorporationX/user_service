package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Collections;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapToId")
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(source = "skillsId", target = "skills", qualifiedByName = "mapToSkillRequest")
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapToId")
    default List<Long> mapToId(List<SkillRequest> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(SkillRequest::getId)
                .toList();
    }

    @Named("mapToSkillRequest")
    default List<SkillRequest> mapToSkillRequest(List<Long> skillsId) {
        if (skillsId == null) {
            return Collections.emptyList();
        }
        return skillsId.stream()
                .map(recommendationRequest -> {
                    SkillRequest skillRequest = new SkillRequest();
                    skillRequest.setId(recommendationRequest);
                    return skillRequest;
                })
                .toList();
    }
}
