package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();
        String message = recommendationRequest.getMessage();
        Optional<RecommendationRequest> request = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (!requester.isPresent() && !receiver.isPresent())
            throw new UserNotFoundException("User not found");
        if (request.isPresent()) {
            if (request.get().getCreatedAt().plusMonths(6).isAfter(recommendationRequest.getCreatedAt())) {
                throw new RequestTimeOutException("Last request was less than 6 months ago");
            }
        }

        recommendationRequest.getSkillsId()
                .forEach(skillRequestId -> {
                    if (!skillRequestRepository.existsById(skillRequestId)) {
                        throw new SkillsNotFoundException("Skills not found");
                    }
                });
        recommendationRequest.getSkillsId()
                .forEach(skillRequestId -> skillRequestRepository.create(recommendationRequest.getId(), skillRequestId));

        return recommendationRequestMapper
                .toDto(recommendationRequestRepository.create(requesterId, receiverId, message));
    }

}
