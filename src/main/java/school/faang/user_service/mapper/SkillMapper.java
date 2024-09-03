package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto skillToSkillDto(Skill skill);
    Skill skillDtoToSkill(SkillDto skillDto);
}
