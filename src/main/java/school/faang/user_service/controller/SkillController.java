package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/{id}")
    @ResponseBody
    public void create(@Valid @RequestBody SkillDto skill){
        skillService.create(skill);
    }
}
