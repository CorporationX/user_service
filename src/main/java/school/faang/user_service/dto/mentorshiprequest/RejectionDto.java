package school.faang.user_service.dto.mentorshiprequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDto {

    @NotBlank(message = "Determine a reason for rejection")
    private String reason;
}
