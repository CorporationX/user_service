package school.faang.user_service.filter.event;

import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
public class EventFilterDto {

    String title;
    Instant startDate;
}
