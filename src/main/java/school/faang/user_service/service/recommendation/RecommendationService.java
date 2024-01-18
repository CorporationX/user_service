package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@Component
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public void deleteRecommendation(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("id not found / id не найден");
        }
        recommendationRepository.deleteById(id);
    }
}