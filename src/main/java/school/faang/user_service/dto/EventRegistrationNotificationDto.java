package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventRegistrationNotificationDto {
    private String userId;
    private String eventId;
    private String message;
    private String telegramId;
}
