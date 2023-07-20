package school.faang.user_service.dto.mentorship;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {

    @NotNull
    private Long receiverId;
    @NotNull
    private Long requesterId;

    @NotBlank
    private String description;

    private String rejectionReason;
    private RequestStatus status;
}
