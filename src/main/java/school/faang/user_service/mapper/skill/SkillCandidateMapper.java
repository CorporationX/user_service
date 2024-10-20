package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    SkillCandidateDto toDto(Skill skill, long offersAmount);
}