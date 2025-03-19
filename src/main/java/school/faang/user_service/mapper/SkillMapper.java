package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring" )
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);

    SkillCandidateDto toSkillCandidateDto(Skill skill);
}
