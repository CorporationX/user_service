package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skill")
@AllArgsConstructor
@Valid
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Validated @RequestBody SkillDto skillRequest) {
        return skillService.create(skillRequest);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<SkillDto> getUserSkills(@NotEmpty @PathVariable Long userId) {
        return skillService.getUsersSkills(userId);
    }

    @GetMapping("/offered/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<SkillCandidateDto> getOfferedSkills(@NotEmpty @PathVariable Long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/{skillId}/user/{userId}/acquire")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SkillDto acquireSkillsFromOffers(@NotEmpty @PathVariable long skillId, @NotEmpty @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
