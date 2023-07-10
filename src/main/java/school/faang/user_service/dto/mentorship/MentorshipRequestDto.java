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

    @NotBlank
    private String description;

    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;

    private String rejectionReason;
    private RequestStatus requestStatus;
}
