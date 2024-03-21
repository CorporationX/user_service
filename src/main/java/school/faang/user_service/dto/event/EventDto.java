package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
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
public class EventDto {
    private Long id;
    @NotBlank(message = "Event must have a title")
    private String title;
    @NotBlank(message = "Event must have a start date")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotBlank(message = "Event must have an owner")
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillsIds;
    private String location;
    private int maxAttendees;
}