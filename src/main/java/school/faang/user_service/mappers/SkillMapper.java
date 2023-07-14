package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDTO(Skill skill);

    Skill toEntity(SkillDto skillDto);

    SkillCandidateDto candidateToDTO(Skill skill);
}
