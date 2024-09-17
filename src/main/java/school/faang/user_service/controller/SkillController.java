package school.faang.user_service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
@Api(description = "Контроллер для управления скиллами")
public class SkillController {
    private final SkillService skillService;

    @ApiOperation("Создание скилла")
    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        if (skillDto.validateTitle()) {
            return skillService.create(skillDto);
        }
        throw new DataValidationException("skill title is invalid");
    }

    @ApiOperation("Получение списка скиллов пользователя по его id")
    @GetMapping("/userSkills")
    public List<SkillDto> getUserSkills(@RequestParam long id) {
        return skillService.getUserSkills(id);
    }

    @ApiOperation("Получение списка скиллов, которые были предложены пользователю")
    @GetMapping("/offeredSkills")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @ApiOperation("Добавляет скилл из имеющихся предложений")
    @PostMapping("/acquireSkills")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
