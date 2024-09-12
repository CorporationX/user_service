package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFilters {
    private String title;
    private LocalDateTime startDate;
    private String location;
    private String ownerUsername;
}