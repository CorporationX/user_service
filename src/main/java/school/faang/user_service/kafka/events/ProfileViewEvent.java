package school.faang.user_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import school.faang.user_service.kafka.Event;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ProfileViewEvent extends Event {
    private Long viewedUserId;
    private Long viewerUserId;
}
