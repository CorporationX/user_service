package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumBoughtEvent {
    private Long userId;
    private int price;
    private int subscriptionDurationInDays;
    private LocalDateTime purchaseDateTime;
}
