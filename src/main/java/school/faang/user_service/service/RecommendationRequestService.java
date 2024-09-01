package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int SIX_MONTH_IN_DAYS = 180;
    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper requestMapper;

    public void create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = requestMapper.toEntity(recommendationRequestDto);
        validateRecommendationRequest(recommendationRequest);
//        checkRequestPeriod(recommendationRequest);
    }

    private void validateRecommendationRequest(RecommendationRequest recommendationRequest) {
        validateUsers(recommendationRequest);
        validateRequestPeriod(recommendationRequest);
        //to be continued
    }

    private void validateUsers(RecommendationRequest recommendationRequest) {
        if (!userRepository.existsById(recommendationRequest.getRequester().getId())) {
            throw new IllegalArgumentException("Requester was not found");
        }

        if (!userRepository.existsById(recommendationRequest.getReceiver().getId())) {
            throw new IllegalArgumentException("Receiver was not found");
        }
    }

    private void validateRequestPeriod(RecommendationRequest recommendationRequest) {
        Optional<RecommendationRequest> latestPendingRequest = requestRepository.
                findLatestPendingRequest(recommendationRequest.getRequester().getId(),
                        recommendationRequest.getReceiver().getId());

        if (latestPendingRequest.isPresent()) {
            if(ChronoUnit.DAYS.between(latestPendingRequest.get().getCreatedAt(), recommendationRequest.getCreatedAt()) <
                    SIX_MONTH_IN_DAYS){
                throw new IllegalArgumentException("60 days must pass for a new request");
            }
        }
    }
}
