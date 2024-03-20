package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {

    private Long id;
    @NotNull
    private String description;
    @NotNull
    private Long requesterId;
    @NotNull
    private Long receiverId;

    private RequestStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
