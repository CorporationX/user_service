package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillDtoSkillMapper {
    Skill toSkill(SkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    SkillCandidateDto toCandidateDto(Skill skill);
}
