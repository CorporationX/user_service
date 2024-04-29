package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Component
@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill DtoToSkill(SkillDto skillDto);
    SkillDto skillToDto(Skill skill);
}
