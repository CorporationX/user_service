package school.faang.user_service.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "ДТО события.")
public class EventDto {
    @Schema(description = "Идентификатор события.")
    private Long id;
    @Schema(description = "Название события.")
    @NotBlank(message = "Не удалось создать событие! Введены не верные данные.")
    @Max(value = 64, message = "Не удалось создать событие! Введены не верные данные.")
    private String title;
    @Schema(description = "Дата и время начала события.")
    @NotNull(message = "Не удалось создать событие! Введены не верные данные.")
    private LocalDateTime startDate;
    @Schema(description = "Дата и время завершения события.")
    private LocalDateTime endDate;
    @Schema(description = "Создатель события.")
    @Positive
    private Long ownerId;
    @Schema(description = "Описание события.")
    private String description;
    @Schema(description = "Идентификаторы навыков, связанных с событинем.")
    private List<Long> relatedSkillsIds;
    @Schema(description = "Место события.")
    private String location;
    @Schema(description = "Не понятное поле.")
    private int maxAttendees;
    @Schema(description = "Тип события.")
    private EventType type;
    @Schema(description = "Статус события.")
    private EventStatus status;
}
