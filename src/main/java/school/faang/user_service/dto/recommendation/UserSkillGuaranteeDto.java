package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSkillGuaranteeDto {
    @Positive(message = "ID must be positive")
    private long id;

    @Positive(message = "UserID must be positive")
    private long userId;

    @Positive(message = "SkillID must be positive")
    private long skillId;

    @Positive(message = "GuarantorID must be positive")
    private long guarantorId;
}
