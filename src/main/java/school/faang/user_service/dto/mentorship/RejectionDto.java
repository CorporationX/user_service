package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RejectionDto {
    @NotNull
    @Length(min = 1, max = 50)
    String reason;
}
