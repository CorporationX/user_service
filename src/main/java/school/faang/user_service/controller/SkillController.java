package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillMapper skillMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@RequestBody @Valid SkillDto skill) {
        Skill entity = skillMapper.toEntity(skill);
        Skill saved = skillService.createSkill(entity);
        return skillMapper.toDto(saved);
    }

    @GetMapping("/user-skills")
    public List<SkillDto> getUserSkills(@RequestParam Long userId) {
        List<Skill> userSkills = skillService.getUserSkills(userId);
        return skillMapper.toDtoList(userSkills);
    }

    @GetMapping("/offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam Long userId) {
        List<Skill> skillCandidate = skillService.getOfferedSkills(userId);
        return skillMapper.toCandidateDtoList(skillCandidate);
    }

    @PutMapping("/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable Long skillId, @RequestParam Long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toDto(skill);
    }
}