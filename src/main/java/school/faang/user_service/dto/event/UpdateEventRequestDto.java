package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequestDto {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    private String description;

    private String location;

    @NotNull
    private Long ownerId;

    @NotNull
    private List<Long> relatedSkills;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Integer maxAttendees;

    @NotNull
    private String eventType;

    @NotNull
    private String eventStatus;
}