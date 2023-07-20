package school.faang.user_service.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public RecommendationRequestDto create (RecommendationRequestDto recommendationRequest) {
        if (!userRepository.existsById(recommendationRequest.getRequesterId()) || !userRepository.existsById(recommendationRequest.getReceiverId())) {
            throw new NotFoundException("User not found");
        } else {
           return null;
        }
    }
}