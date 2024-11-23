package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDate;
@Data
public class EventFilterDto {
    private String title;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private Long ownerId;
}
