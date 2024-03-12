package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;
    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        Optional.ofNullable(skill).orElseThrow(()->new DataValidationException( "Skill is null!" ));
        return skillService.create( skill );
    }
    @GetMapping
    public List<SkillDto> getUserSkills(@RequestParam long userId) {
        return skillService.getUserSkills( userId );
    }
    @GetMapping (value = "/offers")
    List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills( userId );
    }
    @PostMapping(value = "/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers( skillId, userId );
    }
}
