package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestFilterDto {
    private String description;

    @Min(message = "Requester ID must be greater than zero", value = 1)
    private Long requesterId;

    @Min(message = "Receiver ID must be greater than zero", value = 1)
    private Long receiverId;

    @Pattern(message = "Status must be one of PENDING, ACCEPTED, REJECTED", regexp = "PENDING|ACCEPTED|REJECTED|^$")
    private String status;
}
