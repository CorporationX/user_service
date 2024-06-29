package school.faang.user_service.dto.event.redis;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public abstract class Event {
    private Long id;
    private LocalDateTime createdAt;
    
    public Event() {
        createdAt = LocalDateTime.now();
    }
}
