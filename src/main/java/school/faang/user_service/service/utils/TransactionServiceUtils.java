package school.faang.user_service.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.service.user.UserService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionServiceUtils {
    private final UserService userService;

    public Transaction buildTransaction(Long userId, Payable item) {
        return Transaction.builder()
                .transactionNumber(createTransactionNumber())
                .amount(item.getPrice())
                .currencyCode(item.getCurrency())
                .purpose(item.getPurpose())
                .purchaseItem(item.getName())
                .user(userService.getUserById(userId))
                .transactionStatus(TransactionStatus.CREATED)
                .build();
    }

    private Long createTransactionNumber() {
        return UUID.randomUUID().getMostSignificantBits();
    }
}
