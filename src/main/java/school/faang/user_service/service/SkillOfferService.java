package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepo;

    public List<SkillOffer> getAllSkillOffers(Long skillId, Long userId) {
        return skillOfferRepo.findAllOffersOfSkill(skillId, userId);
    }
}
