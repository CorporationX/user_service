package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class MentorshipRejectionDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private String reason;
}
