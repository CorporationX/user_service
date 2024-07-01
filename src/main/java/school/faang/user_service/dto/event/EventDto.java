package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @NotNull
    private Long ownerId;

    @NotNull
    @NotBlank
    private String description;

    private List<Long> relatedSkillsIds;
    private String location;
    private int maxAttendees;
}