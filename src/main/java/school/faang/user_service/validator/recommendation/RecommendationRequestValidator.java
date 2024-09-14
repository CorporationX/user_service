package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private long MONTH_FOR_SEARCH_REQUEST = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public void validateRequesterAndReceiver(RecommendationRequestDto requestDto) {
        if (requestDto.getRequesterId().equals(requestDto.getReceiverId())) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same person.");
        }
        if (!userRepository.existsById(requestDto.getRequesterId())) {
            throw new IllegalArgumentException("Requester does not exist.");
        }
        if (!userRepository.existsById(requestDto.getReceiverId())) {
            throw new IllegalArgumentException("Receiver does not exist.");
        }
    }

    public void validateRequestAndCheckTimeLimit(RecommendationRequestDto requestDto, LocalDateTime localDateTime) {
        if () {
            recommendationRequestRepository.findLatestPendingRequest(requestDto.getRequesterId(), requestDto.getReceiverId())
            if (requestDto.getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(localDateTime)) {
                throw new IllegalArgumentException("Not enough time has passed since the last request.");
            }
        }
    }


}
