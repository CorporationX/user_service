package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class EventDto {
    private Long id;
    @NotBlank(message = "Event must have a title")
    @Size(max = 150, message = "Event title can't be longer than 150 characters")
    private String title;
    @NotNull(message = "Event must have a start date")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "Event must have an owner")
    @Positive(message = "Event owner id can't be less than 1")
    private Long ownerId;
    @Size(max = 500, message = "Event description can't be longer than 500 characters")
    private String description;
    private List<Long> relatedSkillsIds;
    @Size(max = 100, message = "Event location name can't be longer than 100 characters")
    private String location;
    private int maxAttendees;
}