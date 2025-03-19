package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatusDto status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
