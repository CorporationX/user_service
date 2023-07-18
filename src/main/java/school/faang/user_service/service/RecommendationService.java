package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;

@Service
public class RecommendationService {
    RecommendationRepository recommendationRepository;
    SkillOfferRepository skillOfferRepository;
    public void create(RecommendationDto recommendation){

    }
}
