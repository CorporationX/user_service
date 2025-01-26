package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "skillsIds", source = "skills", qualifiedByName = "mapSkillsToIds")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", source = "skillsIds", qualifiedByName = "mapIdsToSkills")
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    @Named("mapSkillsToIds")
    default List<Long> mapSkillsToIds(List<SkillRequest> skillRequests) {
        if (skillRequests == null) {
            return List.of();
        }
        return skillRequests.stream().map(SkillRequest::getId).toList();
    }

    @Named("mapIdsToSkills")
    default List<SkillRequest> mapIdsToSkills(List<Long> skillIds) {
        return skillIds.stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    SkillRequest skillRequest = new SkillRequest();
                    skillRequest.setSkill(skill);
                    return skillRequest;
                })
                .toList();
    }
}
