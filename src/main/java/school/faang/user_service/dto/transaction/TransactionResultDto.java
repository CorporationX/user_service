package school.faang.user_service.dto.transaction;

import lombok.Data;
import school.faang.user_service.entity.transaction.TransactionStatus;

import java.util.Currency;

@Data
public class TransactionResultDto {
    private Long transactionNumber;
    private String message;
    private String purpose;
    private String purchaseItem;
    private Long amount;
    private Currency currency;
    private TransactionStatus status;
}
