package school.faang.user_service.enums.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PremiumStatus {
    PURCHASED("Purchesed"),
    FAILED("Failed"),
    REFUNDED("Refunded");

    private String value;
}
