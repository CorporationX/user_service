package school.faang.user_service.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Component
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    @Autowired
    public SkillOfferService(SkillOfferRepository skillOfferRepository) {
        this.skillOfferRepository = skillOfferRepository;
    }

    public void saveSkillOffers(List<SkillOfferDto> skillOfferList, Long recommendId) {
        List<Long> skillOfferIds = new ArrayList<>();

        skillOfferList.stream()
                .filter(skillOfferDto -> skillOfferDto.skillId() != null && skillOfferDto.skillId() <= 0)
                .forEach(skillOffer -> skillOfferIds.add(skillOfferRepository.create(skillOffer.skillId(), recommendId)));

        if(skillOfferList.size() != skillOfferIds.size()) {
            throw new DataValidationException("Error save skillOffers");
        }
    }

    public void deleteAllSkillOffers(Long recommendationId) {
        skillOfferRepository.deleteAllByRecommendationId(recommendationId);
    }
}
