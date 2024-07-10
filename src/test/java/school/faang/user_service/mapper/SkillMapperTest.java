package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapperTest {

    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skilldto);

    List<SkillDto> toDto(List<Skill> skills);
    List<Skill>  toEntity (List<SkillDto> skillsdto);
}

