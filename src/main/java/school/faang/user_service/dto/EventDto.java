package school.faang.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private long id;

    @Min(1)
    @Max(64)
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long ownerId;

    @Min(1)
    @Max(4096)
    private String description;
    private List<Long> relatedSkillsIds;

    @Min(1)
    @Max(128)
    private String location;
    private int maxAttendees;
}
