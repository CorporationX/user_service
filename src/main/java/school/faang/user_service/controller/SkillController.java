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
import school.faang.user_service.dto.skill.AcquireSkillFromOffersDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

    private static final String EMPTY_TITLE_MSG = "Skill title can't be empty";
    private final SkillService skillService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody SkillDto skillDto) {
        if (!validateSkill(skillDto)) {
            throw new DataValidationException(EMPTY_TITLE_MSG);
        }
        return skillService.create(skillDto);
    }

    public boolean validateSkill(SkillDto skill) {
        boolean result = true;
        if (skill.getTitle().isBlank() || skill.getTitle().isEmpty()) {
            log.error(EMPTY_TITLE_MSG);
            result = false;
        }
        return result;
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") long userId) {
        return skillService.getUserSkills(userId);
    }


    @GetMapping("/offered/user/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/acquire")
    public SkillDto acquireSkillFromOffers(@RequestBody AcquireSkillFromOffersDto acquireDto) {
        return skillService.acquireSkillFromOffers(acquireDto.getSkillId(), acquireDto.getUserId());
    }
}
