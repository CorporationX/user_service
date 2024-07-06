package school.faang.user_service.filter.event;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class EventFilterDto {

    String title;
    LocalDateTime startDate;
}
