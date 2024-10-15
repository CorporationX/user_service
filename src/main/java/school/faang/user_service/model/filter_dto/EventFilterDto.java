package school.faang.user_service.model.filter_dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class EventFilterDto {
    private String titlePattern;
    private Integer maxAttendees;
}
