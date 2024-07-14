package school.faang.service.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.service.user.dto.skill.SkillDto;
import school.faang.service.user.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skill);
}
