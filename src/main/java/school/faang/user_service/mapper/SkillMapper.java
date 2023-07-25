package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;


@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto skillToDto(Skill skill);
    Skill skillToEntity(SkillDto skillDto);
}
