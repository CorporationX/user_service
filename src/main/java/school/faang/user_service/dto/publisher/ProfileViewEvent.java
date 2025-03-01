package school.faang.user_service.dto.publisher;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProfileViewEvent(
        long profileId,
        long viewId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                LocalDateTime timestamp) {}
