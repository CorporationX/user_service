package school.faang.user_service.kafka.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.kafka.Event;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecommendationRequestEvent extends Event {
    private final Long recommendationRequestId;
    private UserDtoNotification author;
    private UserDtoNotification receiver;
}
