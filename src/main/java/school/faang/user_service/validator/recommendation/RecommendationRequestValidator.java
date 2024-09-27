package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final static long MONTH_FOR_SEARCH_REQUEST = 6;

    public void validateUserExistence(boolean exists, long userId) {
        if (!exists) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
    }

    public void validateRequesterAndReceiver(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same person.");
        }
    }

    public void validateRequestAndCheckTimeLimit(LocalDateTime lastRequestTime) {
        if (lastRequestTime != null && lastRequestTime.plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Not enough time has passed since the last request.");
        }
    }
}

