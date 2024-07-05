package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skillService.SkillService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/skill")
@AllArgsConstructor
@Valid
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/create")
    public ResponseEntity<SkillDto> create(@Validated @RequestBody SkillDto skillRequest) {
        SkillDto skillResponse = skillService.create(skillRequest);
        return new ResponseEntity<>(skillResponse, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillDto>> getUserSkills(@NotEmpty @PathVariable Long userId) {
        List<SkillDto> usersSkills = skillService.getUsersSkills(userId);
        return ResponseEntity.ok(usersSkills);
    }

    @GetMapping("/offered/{userId}")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills(@NotEmpty @PathVariable Long userId) {
        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        return ResponseEntity.ok(offeredSkills);
    }

    @GetMapping("/{skillId}/user/{userId}/acquire")
    public ResponseEntity<SkillDto> acquireSkillsFromOffers(@NotEmpty @PathVariable long skillId, @NotEmpty @PathVariable long userId) {
        Optional<SkillDto> acquiredSkill = skillService.acquireSkillFromOffers(skillId, userId);

        return acquiredSkill
                .map(ResponseEntity::ok)
                .orElseGet(() ->ResponseEntity.noContent().build());
    }
}
