package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.service.skillOffer.SkillOfferService;

@RestController
@RequestMapping("/skillOffers")
@RequiredArgsConstructor
public class SkillOfferController {
    private final SkillOfferService skillOfferService;

    @PostMapping
    public SkillOfferDto offerSkill(
        @RequestParam Long senderId,
        @RequestParam Long receiverId,
        @RequestParam Long skillId
    ) {
        return skillOfferService.offerSkillToUser(senderId, receiverId, skillId);
    }
}
