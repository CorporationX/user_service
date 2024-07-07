package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import school.faang.user_service.dto.DtoValidationConstraints;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
public class MentorshipRequestDto {

    private Long id;
    @NotBlank(message = DtoValidationConstraints.MENTORSHIP_REQUEST_DESCRIPTION_CONSTRAINT)
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus requestStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
