package school.faang.user_service.kafka.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumBoughtEvent {
    private Long userId;
    private Double amount;
    private Integer duration;
    private LocalDateTime boughtAt;
}
