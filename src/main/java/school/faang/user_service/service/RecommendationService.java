package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@AllArgsConstructor
public class RecommendationService {
    private RecommendationRepository recommendationRepository;
    private SkillOfferRepository skillOfferRepository;

    private RecommendationDto create(RecommendationDto recommendation) {

    }
}
