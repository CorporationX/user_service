package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);
}
