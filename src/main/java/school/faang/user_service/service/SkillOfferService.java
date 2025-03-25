package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> getSkillRequestsByRecommendationId(long recommendationId) {
        return skillOfferRepository.findAllByRecommendationId(recommendationId);
    }

    public void deleteSkillOffersByRecommendationId(long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }

    public Optional<SkillOffer> findSkillOfferBySkillAndRecommendationIds(long skillId, long recommendationId) {
        return skillOfferRepository.findBySkillIdAndRecommendationId(skillId, recommendationId);
    }

    public void createSkillOffer(long skillId, long recommendationId) {
        skillOfferRepository.create(skillId, recommendationId);
    }
}
