package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RejectionDto {

    @NotBlank(message = "Reason must not be blank")
    private String reason;
}
