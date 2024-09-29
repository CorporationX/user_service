package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {

    @Mapping(source = "requestId", target = "request", qualifiedByName = "mapToRequest")
    @Mapping(source = "skillId", target = "skill", qualifiedByName = "mapToSkill")
    SkillRequest toEntity(SkillRequestDto dto);

    @Named("mapToRequest")
    default RecommendationRequest mapToRequest(Long requestId) {
        return RecommendationRequest.builder().id(requestId).build();
    };

    @Named("mapToSkill")
    default Skill mapToSkill(Long skillId) {
        return Skill.builder().id(skillId).build();
    };

    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "skill.id", target = "skillId")
    SkillRequestDto toDto(SkillRequest entity);
}
