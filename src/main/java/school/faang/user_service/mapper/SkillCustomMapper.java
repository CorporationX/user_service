package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

@Component
public class SkillCustomMapper {
    public SkillDto toDto(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .build();
    }

    public Skill toEntity(SkillDto skillDto) {
        return Skill.builder()
                .id(skillDto.getId())
                .title(skillDto.getTitle())
                .build();
    }
}
