package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RejectionDto {

    @NotBlank(message = "Rejection reason should not be empty.")
    private String rejectionReason;
}
