package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumEvent {
    private long userId;
    private long premiumId;
    private PremiumPeriod premiumPeriod;
    private LocalDateTime createAt;
}
