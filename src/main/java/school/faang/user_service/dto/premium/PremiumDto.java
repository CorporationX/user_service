package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PremiumDto {
    private long premiumId;

    private long userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
