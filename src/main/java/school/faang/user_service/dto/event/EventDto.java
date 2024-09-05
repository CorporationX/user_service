package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventDto {
    private Long id;

    @NotEmpty(message = "Название события не может быть пустым.")
    @Size(min = 1, max = 64, message = "Название события должно быть от 1 до 64 символов")
    private String title;

    @NotNull(message = "Дата начала события не может быть пустой.")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "ID организатора не может быть пустым.")
    private Long ownerId;

    @NotEmpty(message = "Описание события не может быть пустым.")
    @Size(min = 1, max = 4096, message = "Описание события должно быть от 1 до 4096 символов")
    private String description;

    private List<Long> relatedSkillsIds;

    @NotEmpty(message = "Локация события не может быть пустым.")
    @Size(min = 1, max = 128, message = "Локация события должна быть от 1 до 128 символов")
    private String location;

    @NotNull(message = "Тип события не может быть пустым.")
    private EventType type;

    @NotNull(message = "Статус события не может быть пустым.")
    private EventStatus status;

    private int maxAttendees;
}
