package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class MentorshipRequestDto {
    private LocalDateTime time;
    private String description;
    private Long requesterId;
    private Long receiverId;
}
