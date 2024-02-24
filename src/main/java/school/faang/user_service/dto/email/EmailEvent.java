package school.faang.user_service.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmailEvent {
    private long userId;
    private LocalDateTime localDateTime;
}
