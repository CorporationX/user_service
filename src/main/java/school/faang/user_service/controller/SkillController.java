package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillOfferService;
import school.faang.user_service.service.skill.SkillService;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SkillOfferService skillOfferService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("The title is not valid");
        }
    }

    @PostMapping("/{skillId}/user/{userId}")
    public SkillOfferDto createSkillOffer(@Valid @RequestBody SkillOfferDto skillOfferDto,
                                          @PathVariable Long skillId,
                                          @PathVariable Long userId) {
        log.debug("Received request to create skill offer for Skill Id: {} and User Id: {}", skillId, userId);

        return skillOfferService.createSkillOffer(skillOfferDto);
    }

    @GetMapping("/user/{userId}")
    public List<SkillOffer> getAllSkillOffersByUserId(@PathVariable Long userId) {
        return skillOfferService.findAllByUserId(userId);
    }

    @GetMapping("/skill/{skillId}/user/{userId}")
    public List<SkillOffer> getAllSkillOffersOfSkillForUser(
            @PathVariable Long skillId, @PathVariable Long userId) {
        return skillOfferService.findAllOffersOfSkill(skillId, userId);
    }
}