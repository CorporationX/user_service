package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PremiumDto {
    Long id;
    @NotNull
    Long userId;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
