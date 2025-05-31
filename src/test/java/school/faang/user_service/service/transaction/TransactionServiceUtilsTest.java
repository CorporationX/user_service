package school.faang.user_service.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.utils.TransactionServiceUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceUtilsTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private TransactionServiceUtils transactionServiceUtils;

    @Test
    void buildTransaction_Success() {
        User user = new User();
        user.setId(1L);
        Payable payable = PremiumPeriod.MONTHLY;
        when(userService.getUserById(1L)).thenReturn(user);

        Transaction result = transactionServiceUtils.buildTransaction(1L, payable);

        assertNotNull(result);
        assertEquals(payable.getPrice(), result.getAmount());
        assertEquals(payable.getCurrency(), result.getCurrencyCode());
        assertEquals(payable.getPurpose(), result.getPurpose());
        assertEquals(payable.getName(), result.getPurchaseItem());
        assertEquals(user, result.getUser());
        assertEquals(TransactionStatus.CREATED, result.getTransactionStatus());
    }

    @Test
    void buildTransaction_GeneratesUniqueTransactionNumber() {
        User user = new User();
        user.setId(1L);
        Payable payable = PremiumPeriod.MONTHLY;
        when(userService.getUserById(1L)).thenReturn(user);

        Transaction result1 = transactionServiceUtils.buildTransaction(1L, payable);
        Transaction result2 = transactionServiceUtils.buildTransaction(1L, payable);

        assertNotNull(result1.getTransactionNumber());
        assertNotNull(result2.getTransactionNumber());
        assertNotEquals(result1.getTransactionNumber(), result2.getTransactionNumber());
    }
    
}