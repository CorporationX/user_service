package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.util.SkillValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {


    private final SkillService skillService;
    private final SkillValidator validator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validator.validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/offered/user/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/{userId}/offered/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable("userId") long userId, @PathVariable("skillId") long skillId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
