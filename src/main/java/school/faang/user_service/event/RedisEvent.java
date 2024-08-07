package school.faang.user_service.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
public class RedisEvent {

    private final UUID eventId = UUID.randomUUID();

}
