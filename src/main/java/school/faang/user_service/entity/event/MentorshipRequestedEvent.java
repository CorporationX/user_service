package school.faang.user_service.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestedEvent {

    private Long receiverId;
    private Long mentorId;
    private LocalDateTime dateTime;
}


