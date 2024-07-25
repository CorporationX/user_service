package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Component
public class RecommendationRequestIdValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validateId(Long id) {
        if (!recommendationRequestRepository.existsById(id)) {
            throw new NoSuchElementException("There is no recommendation request with id " + id);
        }
    }
}
