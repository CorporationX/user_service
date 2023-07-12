package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping()
    public SkillDto create() {
        SkillDto skillDto = new SkillDto(1L, "crek");
        validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isEmpty()) {
            throw new DataValidationException("Название не может быть пустым!!!");
        }
    }
}
