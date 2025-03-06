package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/create")
    public ResponseEntity<SkillDto> create(
            @RequestBody SkillDto skill
    ) {
        validateSkill(skill);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        skillService.create(skill)
                );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillDto>> getUserSkills(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                skillService
                        .getUserSkills(userId)
        );
    }

    @GetMapping("/user/{userId}/offered-skills")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                skillService
                        .getOfferedSkills(userId)
        );
    }

    @PostMapping("/user/{userId}/acquire-skill/{skillId}")
    public ResponseEntity<SkillDto> acquireSkillFromOffers(
            @PathVariable Long skillId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                skillService
                        .acquireSkillFromOffers(skillId, userId));
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title must not be empty");
        }
    }
}
