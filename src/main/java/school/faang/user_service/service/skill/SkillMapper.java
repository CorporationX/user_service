package school.faang.user_service.service.skill;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;

@Component
public class SkillMapper {
    public Skill toSkill(CreateSkillDto dto) {
        if (dto == null) {
            return null;
        }

        Skill skill = new Skill();
        skill.setTitle(dto.title());
        return skill;

    }

    public SkillDto toSkillDto(Skill entity) {
        if (entity == null) {
            return null;
        }

        return  new SkillDto(entity.getId(), entity.getTitle());
    }
}
