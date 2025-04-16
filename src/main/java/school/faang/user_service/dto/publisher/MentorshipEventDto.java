package school.faang.user_service.dto.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MentorshipEventDto {

    private long mentorId;
    private long menteeId;
    private LocalDateTime createdAt;
}
