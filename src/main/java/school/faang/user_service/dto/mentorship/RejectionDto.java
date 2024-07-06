package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionDto {

    @NotNull(message = "Reason of rejection shouldn't be null")
    @NotBlank(message = "Message can not be blank")
    private String rejectionReason;
}
