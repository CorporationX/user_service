package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

@Component
public class SkillController {
    private SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    public SkillDto create(SkillDto skillDto) {

    }

    public void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null) {

        } else {

        }
    }
}
