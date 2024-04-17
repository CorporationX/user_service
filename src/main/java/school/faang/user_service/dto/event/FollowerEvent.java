package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder

public class FollowerEvent {

    @NotNull
    private long followerId;

    @NotNull
    private long followeeId;

    @NotNull
    private LocalDateTime subscriptionDateTime;

}