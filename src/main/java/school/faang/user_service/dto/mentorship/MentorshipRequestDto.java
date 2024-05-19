package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {
    @NotBlank(message = "The reason for the request must be completed")
    private String description;
    @NotBlank(message = "Requester id must be filled in")
    private long requesterId;
    @NotBlank(message = "Receiver id must be filled in")
    private long idReceiver;
    private RequestStatus status;
    private String rejectionReason;
}
