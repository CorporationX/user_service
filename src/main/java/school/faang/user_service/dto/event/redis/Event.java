package school.faang.user_service.dto.event.redis;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public abstract class Event {
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdAt = LocalDateTime.now();
}
