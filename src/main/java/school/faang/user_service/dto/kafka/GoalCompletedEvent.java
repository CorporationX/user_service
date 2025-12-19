package school.faang.user_service.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoalCompletedEvent(
        @JsonProperty("userId") Long userId,
        @JsonProperty("goalId") Long goalId
) {
}
