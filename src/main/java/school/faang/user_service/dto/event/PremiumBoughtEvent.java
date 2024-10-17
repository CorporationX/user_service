package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record PremiumBoughtEvent(
    long userId,
    @JsonIgnore
    double amount,
    @JsonIgnore
    int duration,
    LocalDateTime timestamp
) {
}
