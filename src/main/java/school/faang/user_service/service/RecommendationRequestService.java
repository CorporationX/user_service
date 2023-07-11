package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRepository;
    private final SkillRequestRepository skillRepository;

    public void create(RecommendationRequestDto recommendationRequest) {
        int receiverId = recommendationRequest.getRecieverId();
        int requesterId = recommendationRequest.getRequesterId();
        RecommendationRequest lastRequest;


    }
}
