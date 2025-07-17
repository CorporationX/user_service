package school.faang.user_service.service;

import school.faang.user_service.entity.Skill;

import java.util.List;

public interface SkillService {
    List<Skill> getSkillEntityListByIds(List<Long> skillIds);
}
