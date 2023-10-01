package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequestDto {
    private Long id;
    @NotBlank
    @Size(max = 150)
    private String description;
    @NotNull
    private long requesterId;
    @NotNull
    private long receiverId;
    private RequestStatus status;
    private String rejectionReason;
}
