package school.faang.user_service.dto.event;

import lombok.Value;
import school.faang.user_service.dto.UserReadDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Value
public class ReadEvetDto {

    Long id;
    String title;
    Instant startDate;
    LocalDateTime endDate;
    UserReadDto owner;
    String description;
    String location;
    int maxAttendees;
}
