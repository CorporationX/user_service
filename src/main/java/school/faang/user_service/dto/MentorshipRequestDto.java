package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @NotNull(message = "Requester ID cannot be empty.")
    @Min(message = "Requester ID must be greater than zero", value = 1)
    private Long requesterId;

    @NotNull(message = "Receiver ID must not be null")
    @Min(message = "Receiver ID must be greater than zero", value = 1)
    private Long receiverId;

    private String rejectionReason;

    private RequestStatus status;
}
