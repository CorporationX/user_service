package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Validated
@Component
public class SkillController {
    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    public SkillDto create(@Valid SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(@Positive @NotNull
                                        Long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(@Positive
                                                    @NotNull
                                                    Long userId) {
          return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId,
                                           @Positive
                                           @NotNull
                                           Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
