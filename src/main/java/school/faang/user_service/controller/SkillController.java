package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
public class SkillController {
    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skill/{userId}/offered/{skillId}")
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    @GetMapping("/skill/{userId}/offered")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }
    @GetMapping("/skill/{userId}/offered/candidates")
    public List<SkillCandidateDto>getOfferedSkills(@PathVariable long userId){
        return skillService.getOfferedSkills(userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cannot be empty");
        }
    }
        @PostMapping("/skill/{userId}/offered/{skillId}/acquire")
        public SkillDto acquireSkillFromOffers(@PathVariable long skillid, @PathVariable long userid){
            return skillService.acquireSkillFromOffers(skillid, userid);
        }
    }

