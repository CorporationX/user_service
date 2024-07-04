package school.faang.user_service.dto.event;

import lombok.Value;
import school.faang.user_service.dto.UserReadDto;

import java.time.LocalDateTime;

@Value
public class EventReadDto {

    Long id;
    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    UserReadDto owner;
    String description;
    String location;
    int maxAttendees;
}
