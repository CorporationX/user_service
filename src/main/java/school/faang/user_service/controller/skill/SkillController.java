package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.service.skill.interfaces.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/user-skills/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") Long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("skills-offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") Long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/acquire/{skillId}/user/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable("skillId") Long skillId, @PathVariable("userId") Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
