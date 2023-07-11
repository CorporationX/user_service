package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor

public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
}
