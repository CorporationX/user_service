package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.dto.skill.SkillDto;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skill);
}
