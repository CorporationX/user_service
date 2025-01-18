package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService service;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Title can not be empty and null");
        }
        return service.createSkill(skillDto);
    }

    @GetMapping("/user")
    public List<SkillDto> getUserSkills(@RequestParam long userId) {
        return service.getUserSkills(userId);
    }

    @GetMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return service.getOfferedSkills(userId);
    }

    @PostMapping("/acquire")
    public SkillDto acquireSkillFromOffered(@RequestParam long skillId, @RequestParam long userId) {
        return service.acquireSkillFromOffer(skillId, userId);
    }
}
