package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PremiumBoughtEvent {
    private long receiverId;
    private int amountPayment;
    private int daysSubscription;
    private LocalDateTime receivedAt;
}
