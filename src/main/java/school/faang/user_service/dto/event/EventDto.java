package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private Long id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    @NotNull(message = "Start date to be specified")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @NotNull(message = "Start date to be specified")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    @NotNull(message = "Id of owner must be specified")
    private Long ownerId;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private List<SkillDto> relatedSkills;
    @NotNull
    private String location;
    @NotNull(message = "You need to specify the max count attendees")
    private Integer maxAttendees;
    @NotNull
    private EventType type;
    @NotNull
    private EventStatus status;
}