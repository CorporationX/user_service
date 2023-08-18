package school.faang.user_service.dto.mentorship;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {

    private long id;
    @NotBlank(message = "Add a description to your mentoring request")
    private String description;
    private long requester;
    private long receiver;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
