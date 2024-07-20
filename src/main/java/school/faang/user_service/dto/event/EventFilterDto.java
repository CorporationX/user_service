package school.faang.user_service.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ДТО фильтров события.")
public class EventFilterDto {
    @Schema(description = "Дата и время начала события.")
    private LocalDateTime startDate;
    @Schema(description = "Дата и время завершения события.")
    private LocalDateTime endDate;
    @Schema(description = "Место события.")
    private String location;
    @Schema(description = "Не понятное поле.")
    private int maxAttendees;
    @Schema(description = "Создатель события.")
    private Long ownerId;
    @Schema(description = "Идентификаторы навыков, связанных с событинем.")
    private List<Long> relatedSkillIds;
    @Schema(description = "Тип события.")
    private EventType type;
    @Schema(description = "Статус события.")
    private EventStatus status;
    @Schema(description = "Дата и время создания события.")
    private LocalDateTime createdAt;
    @Schema(description = "Дата и время обновления события.")
    private LocalDateTime updatedAt;
}
