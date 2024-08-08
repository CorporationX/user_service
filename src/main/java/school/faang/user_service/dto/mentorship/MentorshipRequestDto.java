package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipRequestDto {
    private long id;

    @NotBlank(message = "Description should not be null value")
    @Size(min = 1, max = 4096, message = "Description size should be between 1 and 4096 characters")
    private String description;

    @Min(value = 1, message = "Requester id should not be less than 1")
    private long requesterId;

    @Min(value = 1, message = "Receiver id should not be less than 1")
    private long receiverId;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
