package school.faang.user_service.model.dto.goal;

import lombok.Builder;

@Builder
public record GoalNotificationDto(
        String title,
        String description
) {
}
