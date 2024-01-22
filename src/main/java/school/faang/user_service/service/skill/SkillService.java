package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.List;

public class SkillService {

    public List<SkillDto> getUserSkills(long userId) {
        return new ArrayList<SkillDto>();
    }

    public Skill findById(Long skillId) {
        return new Skill();
    }
}
