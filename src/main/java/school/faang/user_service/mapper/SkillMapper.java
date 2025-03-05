package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toSkill(SkillDto skillDto);
    SkillDto toSkillDto(Skill skill);
}
