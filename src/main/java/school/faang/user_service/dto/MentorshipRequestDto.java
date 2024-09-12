package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {
    private Long id;

    @NotNull(message = "description can't be null")
    private String description;

    @NotNull(message = "requester id can't be null")
    private Long userRequesterId;

    @NotNull(message = "receiver id can't be null")
    private Long userReceiverId;

    @NotNull(message = "status can't be null")
    private String status;

    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
