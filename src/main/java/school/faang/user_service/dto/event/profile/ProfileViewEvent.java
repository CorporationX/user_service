package school.faang.user_service.dto.event.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.event.Event;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProfileViewEvent implements Event {
    private long userId;
    private long viewerId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime viewedAt;
}

