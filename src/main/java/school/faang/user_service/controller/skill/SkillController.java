package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody SkillDto skillDto)
    {
        return skillService.create(skillDto);
    }

    @GetMapping("/users")
    public List<?> getUserSkills(@PathVariable("userId") long userId,
                                 @RequestParam(name = "isOffered", required = false, defaultValue = "false") boolean isOffered) {
        if (isOffered) {
            return skillService.getOfferedSkills(userId);
        }
        return skillService.getUserSkills(userId);
    }

    @PostMapping ("/offered")
    public SkillDto acquireSkillFromOffers(@PathVariable("userId") long userId, @PathVariable("skillId") long skillId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}