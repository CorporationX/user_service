package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    @NotBlank(message = "Не удалось создать событие! Введены не верные данные.")
    @Max(value = 64, message = "Не удалось создать событие! Введены не верные данные.")
    private String title;
    @NotNull(message = "Не удалось создать событие! Введены не верные данные.")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Positive
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillsIds;
    private String location;
    private int maxAttendees;
}
