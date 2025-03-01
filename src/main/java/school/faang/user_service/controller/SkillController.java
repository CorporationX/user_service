package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class SkillController {

    public static final String SKILLS_PATH = "/skills";
    public static final String USER_SKILLS_PATH = SKILLS_PATH + "/{userId}";
    private SkillService service;

    @PostMapping(SKILLS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return service.create(skill);
    }

    @GetMapping(USER_SKILLS_PATH)
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return service.getUserSkills(userId);

    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return service.getOfferedSkills( userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("title не должен быть пустым");
        }
    }

}
