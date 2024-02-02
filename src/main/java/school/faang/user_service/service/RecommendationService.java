package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }
}
