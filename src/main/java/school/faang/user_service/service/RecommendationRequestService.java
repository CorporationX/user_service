package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Optional;

@Service
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository, SkillRequestRepository skillRequestRepository, UserRepository userRepository, RecommendationRequestMapper recommendationRequestMapper) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.skillRequestRepository = skillRequestRepository;
        this.userRepository = userRepository;
        this.recommendationRequestMapper = recommendationRequestMapper;
    }

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();
        String message = recommendationRequest.getMessage();
        Optional<RecommendationRequest> request = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (!requester.isPresent() && !receiver.isPresent())
            throw new NullPointerException("User not found");
        if (request.isPresent()) {
            if (request.get().getCreatedAt().plusMonths(6).isAfter(recommendationRequest.getCreatedAt())) {
                throw new IllegalArgumentException("Last request was less than 6 months ago");
            }
        }

        recommendationRequest.getSkills()
                .forEach(skillRequestDto -> {
                    if (!skillRequestRepository.existsById(skillRequestDto.getSkillId())) {
                        throw new NullPointerException("Skills not found");
                    }
                });
        recommendationRequest.getSkills()
                .forEach(skillRequestDto -> skillRequestRepository.create(skillRequestDto.getId(), skillRequestDto.getSkillId()));

        return recommendationRequestMapper
                .toDto(recommendationRequestRepository.create(requesterId, receiverId, message));
    }

}
