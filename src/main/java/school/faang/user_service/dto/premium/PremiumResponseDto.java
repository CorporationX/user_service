package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.premium.PremiumType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumResponseDto {
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PremiumType premiumType;
}
