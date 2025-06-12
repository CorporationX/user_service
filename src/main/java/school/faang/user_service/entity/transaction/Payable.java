package school.faang.user_service.entity.transaction;

import java.math.BigDecimal;
import java.util.Currency;

public interface Payable {
    TransactionPurpose getPurpose();

    BigDecimal getPrice();

    Currency getCurrency();

    String getName();
}
