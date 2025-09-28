package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionDto {
    @NotBlank(message = "Причина отклонения не должна быть пустой:")
    private String reason;
}

