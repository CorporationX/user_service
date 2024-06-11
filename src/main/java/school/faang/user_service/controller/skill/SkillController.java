package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillValidator skillValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody SkillDto skillDto)
    {
        skillValidator.validateSkillTitle(skillDto);
        return skillService.create(skillDto);
    }

    @GetMapping ("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping ("/offered/user/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping ("/{userId}/offered/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable("userId") long userId, @PathVariable("skillId") long skillId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}