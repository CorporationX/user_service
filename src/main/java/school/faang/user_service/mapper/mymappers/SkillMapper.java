package school.faang.user_service.mapper.mymappers;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mydto.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDTO(Skill skill);

    Skill toEntity(SkillDto skillDto);
}
