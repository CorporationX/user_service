package school.faang.user_service.message.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileViewEvent(
        long actorId,
        long receiverId,
        @JsonFormat
        LocalDateTime receivedAt
        ) {
}
