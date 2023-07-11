package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/skill", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkillController {

    private final SkillService skillService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isEmpty()) {
            throw new DataValidationException("Название не может быть пустым!!!");
        }
    }
}
