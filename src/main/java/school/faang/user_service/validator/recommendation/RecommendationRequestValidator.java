package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {

    private final static long MONTH_FOR_SEARCH_REQUEST = 6;

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

//    public void validateSkillRequests(List<Long> skillIds, List<Long> existingSkillIds) {
//        skillIds.forEach(skillId -> {
//            if (!existingSkillIds.contains(skillId)) {
//                throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist.");
//            }
//        });
//    }

}

