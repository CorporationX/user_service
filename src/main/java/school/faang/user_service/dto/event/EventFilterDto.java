package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    @Size(max = 255, message = "Event title pattern can't be longer than 255 characters")
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    @Size(max = 255, message = "Event description pattern can't be longer than 255 characters")
    private String description;
    private List<Long> relatedSkillsIds;
    @Size(max = 255, message = "Event location pattern can't be longer than 255 characters")
    private String location;
    private int maxAttendees;
}
