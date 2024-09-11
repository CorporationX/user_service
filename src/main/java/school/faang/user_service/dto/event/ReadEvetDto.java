package school.faang.user_service.dto.event;

import lombok.Value;
import school.faang.user_service.dto.user.UserReadDto;

import java.time.LocalDateTime;

@Value
public class ReadEvetDto {

    Long id;
    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    UserReadDto owner;
    String description;
    String location;
    int maxAttendees;
}
