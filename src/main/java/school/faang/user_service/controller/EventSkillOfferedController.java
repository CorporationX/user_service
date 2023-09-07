package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.service.SkillOfferService;
import java.util.List;

@RestController
@RequestMapping("/skills-offered")
@RequiredArgsConstructor
@Slf4j
public class EventSkillOfferedController {
    private final SkillOfferService skillOfferService;
    @PostMapping("/{skillId}/user/{userId}")
    public EventSkillOfferedDto createSkillOffer(@Valid @RequestBody EventSkillOfferedDto eventSkillOfferedDto,
                                                 @PathVariable Long skillId,
                                                 @PathVariable Long userId) {
        log.debug("Received request to create skill offer for Skill Id: {} and User Id: {}", skillId, userId);

        return skillOfferService.createSkillOffer(eventSkillOfferedDto);
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