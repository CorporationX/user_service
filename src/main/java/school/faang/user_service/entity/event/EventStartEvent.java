package school.faang.user_service.entity.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EventStartEvent {

    @NotNull
    private Long eventId;
    @NotNull
    private List<Long> participantIds;

}
