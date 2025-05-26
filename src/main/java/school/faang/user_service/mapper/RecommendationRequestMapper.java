package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rejectionReason", ignore = true)
    @Mapping(target = "requester", ignore = true)  // Добавлено
    @Mapping(target = "receiver", ignore = true)   // Добавлено
    @Mapping(target = "recommendation", ignore = true) // Добавлено
    @Mapping(target = "status", constant = "PENDING")
    RecommendationRequest toEntity(RecommendationRequestDto dto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    RecommendationResponseDto toDto(RecommendationRequest entity);

    default SkillRequest map(String skillName) {
        SkillRequest skillRequest = new SkillRequest();
        Skill skill = new Skill();
        skill.setTitle(skillName);
        skillRequest.setSkill(skill);
        return skillRequest;
    }

    default String map(SkillRequest skill) {
        return skill.getSkill().getTitle();
    }
}