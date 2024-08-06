package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Builder
@Data
public class MentorshipRequestDto {
    private Long id;
    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "requester Id is required")
    private Long requesterId;

    @NotNull(message = "receiver Id is required")
    private Long receiverId;

    private RequestStatus status;
    private String rejectionReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
