package school.faang.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;
    public SkillController(SkillService skillService) {
        this.skillService = skillService;

    }
   @PostMapping(value = "/create")
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill( skill ); //TODO: мне кажется отсюда нужно убрать validateSkill() так как он же внутри сервиса валидирует
        return skillService.create( skill );
    }

    @GetMapping
    public List<SkillDto> getUserSkills(@RequestParam long userId) {
        return skillService.getUserSkills( userId );
    }

    @GetMapping(value = "/offers")
    List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills( userId );
    }

    @PostMapping(value = "/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers( skillId, userId );

    }
    private void validateSkill(SkillDto skill) {
        String title = skill.getTitle();
        if (title == null || title.isEmpty()) {
            throw new DataValidationException( "Skill must have name!" );
        }
    }
}
