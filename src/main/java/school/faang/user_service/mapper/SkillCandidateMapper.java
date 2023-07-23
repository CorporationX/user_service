package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillCandidateMapper {
    Skill skillToEntity(SkillCandidateDto skillCandidateDto);
    SkillCandidateDto skillToDto(Skill skill);
}
