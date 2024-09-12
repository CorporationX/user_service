package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void create(RecommendationRequestDto recommendationRequest){

    }
}
