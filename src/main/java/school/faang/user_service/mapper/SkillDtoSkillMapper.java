package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SkillDtoSkillMapper {
    Skill toSkill(SkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    List<SkillDto> toSkillDtoList(List<Skill> skills);

    SkillCandidateDto toCandidateDto(Skill skill);
}
