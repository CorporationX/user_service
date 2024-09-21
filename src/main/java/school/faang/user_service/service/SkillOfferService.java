package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {

    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> findAllOffersOfSkill(Skill skill, User user) {
        return skillOfferRepository.findAllOffersOfSkill(skill.getId(), user.getId());
    }
}
