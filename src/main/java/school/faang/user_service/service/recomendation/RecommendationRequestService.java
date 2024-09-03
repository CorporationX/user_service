package school.faang.user_service.service.recomendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

@Component
public class RecommendationRequestService {
    private RecommendationRequestRepository recommendationRequestRepository;
    private UserRepository userRepository;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository) {
        this.recommendationRequestRepository = recommendationRequestRepository;
    }

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {

        Optional<User> requesterId = userRepository.findById(recommendationRequestDto.getRequesterId());
        Optional<User> receiverId = userRepository.findById(recommendationRequestDto.getRequesterId());
        if (requesterId.isPresent() && receiverId.isPresent()) {
            return recommendationRequestRepository.save(recommendationRequestDto);
        } else {
            throw new IllegalArgumentException("Requester id or receiver id is wrong");
        }
    }
}
