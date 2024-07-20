package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.PremiumPeriod;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PremiumDto {
    private long premiumId;

    private long userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
