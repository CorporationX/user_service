package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping(params = "userId")
    public List<SkillDto> getUserSkills(@RequestParam Long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping(value = "/offered", params = "userId")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping(value = "/acquire", params = {"skillId", "userId"})
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
