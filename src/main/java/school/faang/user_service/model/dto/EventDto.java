package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    @NotNull
    private Long id;
    private String title;
    private Integer maxAttendees;
    private List<Long> attendeeIds;
}