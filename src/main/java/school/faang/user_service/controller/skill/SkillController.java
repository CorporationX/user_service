package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/skills")
@RestController
public class SkillController {
    private final SkillService skillService;
    private final SkillMapper skillMapper;

    @PostMapping
    public ResponseEntity<SkillDto> create(@RequestBody @Valid SkillDto skillDto) {
        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillService.create(skill);
        SkillDto responseSkill = skillMapper.toSkillDto(skill);
        return ResponseEntity.ok(responseSkill);
    }

    @GetMapping("{userId}/user-skills")
    public ResponseEntity<List<SkillDto>> getUserSkills(
            @PathVariable @Positive long userId) {
        List<Skill> skills = skillService.getUserSkills(userId);
        List<SkillDto> responseSkills = skillMapper.toSkillDtoList(skills);
        return ResponseEntity.ok(responseSkills);
    }

    @GetMapping("{userId}/offered-skills")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills(
            @PathVariable @Positive long userId) {
        List<Skill> skills = skillService.getOfferedSkills(userId);
        List<SkillCandidateDto> responseSkills = skillMapper.toSkillCandidateDtoList(skills);
        return ResponseEntity.ok(responseSkills);
    }

    @PostMapping("{userId}/{skillId}")
    public ResponseEntity<SkillDto> acquireSkillFromOffers(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long skillId) {
        Skill skill = skillService.acquireSkillFromOffers(userId, skillId);
        SkillDto responseSkill = skillMapper.toSkillDto(skill);
        return ResponseEntity.ok(responseSkill);
    }
}
