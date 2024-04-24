package school.faang.user_service.dto.event;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EventStartEvent {

    @NotNull
    long event_id;

    @NotNull
    List<Long> attendeeIds;

}
