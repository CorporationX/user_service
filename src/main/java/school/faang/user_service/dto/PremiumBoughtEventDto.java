package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PremiumBoughtEventDto {
    private Long receiverId;
    private int amountPayment;
    private int daysSubscription;
    private LocalDateTime receivedAt;
}
