package school.faang.user_service.kafka.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.kafka.Event;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecommendationRequestEvent extends Event {
    private final Long recommendationId;
    private UserDto author;
    private UserDto receiver;
}
