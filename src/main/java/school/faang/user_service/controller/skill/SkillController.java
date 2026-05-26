package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillServiceImpl;

@RequestMapping("api/v1/skills")
@RequiredArgsConstructor
@RestController
public class SkillController {
    private final SkillServiceImpl skillService;

    @PostMapping
    public SkillDto create(@RequestBody CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }
}
