package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 64)
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long ownerId;

    @Size(min = 1, max = 4096)
    private String description;
    private List<Long> relatedSkillsIds;

    @Size(min = 1, max = 128)
    private String location;
    private int maxAttendees;
}
