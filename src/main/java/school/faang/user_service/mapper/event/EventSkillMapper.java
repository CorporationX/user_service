package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface EventSkillMapper {
    SkillDto toDto(Skill skill);
    Skill toEntity(SkillDto skillDto);
}
