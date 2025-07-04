package school.faang.user_service.service.skill_offer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillOfferServiceImpl implements SkillOfferService {

    private final SkillOfferRepository skillOfferRepository;

    @Override
    public List<SkillOffer> getAllOffersOfSkill(long skillId, long userId) {
        return skillOfferRepository.findAllOffersOfSkill(skillId, userId);
    }

    @Override
    public int countAllOffersOfSkill(long skillId, long userId) {
        return skillOfferRepository.countAllOffersOfSkill(skillId, userId);
    }
}
