package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RequestNotFoundException;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {

        if (recommendationRequest.getRequesterId() == null || recommendationRequest.getReceiverId() == null)
            throw new UserNotFoundException("User not found");
        if (recommendationRequest.getCreatedAt().plusMonths(6).isAfter(recommendationRequest.getUpdatedAt())) {
            throw new RequestTimeOutException("Last request was less than 6 months ago");
        }

        try {
            recommendationRequest.getSkillsId()
                    .forEach(skillRequestId -> {
                        skillRequestRepository.existsById(skillRequestId.getSkillId());
                    });
        } catch (NullPointerException e) {
            throw new SkillsNotFoundException("Skills not found");
        }
        recommendationRequest.getSkillsId()
                .forEach(skillRequestId -> skillRequestRepository.create(skillRequestId.getId(), skillRequestId.getSkillId()));

        return recommendationRequestMapper
                .toDto(recommendationRequestRepository.create(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId(), recommendationRequest.getMessage()));
    }

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        return recommendationRequestMapper.toDto(request.orElseThrow(() -> new RequestNotFoundException("Request not found by id: " + id)));
    }

}
