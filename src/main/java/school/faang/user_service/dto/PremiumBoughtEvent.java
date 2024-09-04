package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
public class PremiumBoughtEvent {
    private Long userId;
    private Long paymentAmount;
    private Long subscriptionDuration;
    private LocalDateTime timestamp;
}