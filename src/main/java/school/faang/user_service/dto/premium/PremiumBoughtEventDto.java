package school.faang.user_service.dto.premium;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@RequiredArgsConstructor
public class PremiumBoughtEventDto {
    private final long userId;
    private final double cost;
    private final int days;
    private LocalDateTime purchaseDate = LocalDateTime.now();
}
