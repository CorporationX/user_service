package school.faang.user_service.service.skill;


import school.faang.user_service.dto.entity.Skill;

import java.util.List;

public interface SkillService {
    List<Skill> getSkillListBySkillIds(List<Long> ids);
}
