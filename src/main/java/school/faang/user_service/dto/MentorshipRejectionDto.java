package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRejectionDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String reason;
}
