package school.faang.user_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.kafka.Event;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ProfilePicEvent extends Event {
    private Long userId;
    private String url;
}
