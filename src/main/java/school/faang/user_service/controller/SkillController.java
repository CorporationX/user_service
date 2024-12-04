package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(@Valid SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(@Positive @NotNull Long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(
            @Positive @NotNull Long userId
    ) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(@Positive @NotNull Long skillId,
                                           @Positive @NotNull Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}