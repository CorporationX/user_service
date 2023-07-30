package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        recommendationRequestValidator.validateRecommendationRequest(dto);
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        Long requestId = dto.getRequesterId();
        Long receiverId = dto.getReceiverId();
        String message = recommendationRequest.getMessage();

        if (userRepository.existsById(requestId) && userRepository.existsById(receiverId)) {
            if (!recommendationRequestValidator.hasPending(requestId, receiverId)) {
                recommendationRequest.setStatus(RequestStatus.PENDING);

                RecommendationRequest savedRequest = recommendationRequestRepository.
                        create(requestId, receiverId, message);

                for (SkillRequest skillRequest : recommendationRequest.getSkills()) {
                    skillRequest.setRequest(savedRequest);
                    skillRequestRepository.create(requestId, skillRequest.getId());
                }
            } else {
                throw new IllegalArgumentException("A recommendation request between the same users can only be sent once every six months!");
            }
        } else {
            throw new IllegalArgumentException("Requester or receiver doesn't exist!");
        }
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(recommendationRequest));
    }
}