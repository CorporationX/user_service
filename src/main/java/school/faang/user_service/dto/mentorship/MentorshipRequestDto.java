package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;


@Data
public class MentorshipRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
