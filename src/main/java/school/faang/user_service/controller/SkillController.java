package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestParam @NotEmpty(message = "Title can't be empty")
                               @Size(max = 100, message = "Title should be at least 3 symbols short") String title) {
        SkillDto skill = new SkillDto(0L, title);

        return skillService.create(skill);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable Long userId,
                                        @RequestParam("page number") int pageNumber,
                                        @RequestParam("element number") int pageSize) {
        return skillService.getUserSkills(userId, pageNumber, pageSize);
    }

    @GetMapping("/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable Long userId){
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId){
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
