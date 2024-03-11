package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    @Transactional
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<Recommendation> getAllUserRecommendations(long recieverId) {
        return recommendationRepository.findAllByReceiverId(recieverId);
    }
}