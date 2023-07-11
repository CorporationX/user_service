package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    Skill skillToEntity(SkillDto skillDto);
    SkillDto skillToDto(Skill skill);
}
