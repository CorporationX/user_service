package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;

@Component
@Data
public class EventDto {
    @NotBlank @NotNull
    private String title;

    @NotBlank @NotNull
    private String description;

    @NotBlank @NotNull
    private LocalDateTime startDate;

    @NotBlank @NotNull
    private LocalDateTime endDate;

    @NotBlank @NotNull
    private long ownerId;

    @NotBlank @NotNull
    private LocalDateTime createdAt;

    @NotBlank @NotNull
    private LocalDateTime updatedAt;

    @NotBlank @NotNull
    private EventType eventType;

    @NotBlank @NotNull
    private EventStatus status;
}
