package school.faang.user_service.controller;

import school.faang.user_service.dto.SkillDto;

public class SkillController {
    public SkillDto create(SkillDto skill) {
        return null;
    }

    private SkillDto validateSkillDto(SkillDto skill) {
        if (skill.getId() != null && !skill.getTitle().isEmpty()) {
            return skill;
        } else throw new IllegalArgumentException();
    }
}
