package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class RecommendationRequestRepositoryValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validateId(Long id) {
        if (recommendationRequestRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("There is no recommendation request with id " + id);
        }
    }
}
