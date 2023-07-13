package school.faang.user_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
public class SkillController {
    private SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId){
        return skillService.acquireSkillFromOffers(skillId,userId);
    }
}