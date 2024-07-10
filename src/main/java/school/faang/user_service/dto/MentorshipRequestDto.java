package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
