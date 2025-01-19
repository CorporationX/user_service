package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toSkillEntity(SkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    List<SkillDto> toSkillListDto(List<Skill> skills);

    List<SkillCandidateDto> toSkillCandidateDto(List<Skill> skills);
}
