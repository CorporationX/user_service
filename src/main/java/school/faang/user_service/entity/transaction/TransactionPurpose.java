package school.faang.user_service.entity.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionPurpose {
    PREMIUM("Premium"),
    PROMOTION("Promotion"),
    ;
    private final String purpose;
}
