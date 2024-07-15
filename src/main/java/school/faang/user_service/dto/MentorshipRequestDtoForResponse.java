package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDtoForResponse {
    private Long id;

    @NotBlank
    private String description;

    @Positive
    private Long requesterId;

    @Positive
    private Long receiverId;

    private RequestStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
