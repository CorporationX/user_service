package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDto(Skill skill);
    Skill toSkill(SkillDto skillDto);
    List<SkillDto> toDtoList(List<Skill> skills);
    List<Skill> toSkillList(List<SkillDto> skillDtos);
}
