package school.faang.user_service.dto.goal;

import lombok.Builder;

import java.util.List;

@Builder
public record GoalCompletedEvent(List<Long> userIds, long goalId) {
}
