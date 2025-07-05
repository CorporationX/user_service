package school.faang.user_service.kafka.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import school.faang.user_service.kafka.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PremiumBoughtEvent extends Event {
    private AnalyticsEventType eventTypeEnum;
    private BigDecimal paymentAmount;
    private Integer subscriptionDuration;
    private LocalDateTime sentAt;
}
