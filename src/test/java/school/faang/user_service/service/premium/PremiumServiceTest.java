package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.mapper.TransactionMapperImpl;
import school.faang.user_service.service.transaction.TransactionService;
import school.faang.user_service.service.utils.PremiumServiceUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class PremiumServiceTest {
    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PremiumServiceUtils premiumServiceUtils;

    @Spy
    private TransactionMapperImpl transactionMapper;

    @Test
    void shouldBuyPremiumSuccessfully() {
        Long userId = 1L;
        Integer premiumDuration = 30;
        Transaction transaction = new Transaction();
        transaction.setTransactionStatus(TransactionStatus.SETTLED);

        doNothing().when(premiumServiceUtils).checkUserHasNoPremium(userId);
        doNothing().when(premiumServiceUtils).assignPremiumToUser(userId, premiumDuration);
        when(transactionService.buyItem(userId, PremiumPeriod.MONTHLY)).thenReturn(transaction);

        TransactionResultDto actual = premiumService.buyPremium(userId, premiumDuration);

        assertEquals(TransactionStatus.SETTLED, actual.getStatus());
    }

    @Test
    void shouldNotAssignPremiumWhenTransactionFailed() {
        Long userId = 1L;
        Integer premiumDuration = 30;
        Transaction transaction = new Transaction();
        transaction.setTransactionStatus(TransactionStatus.FAILED);

        doNothing().when(premiumServiceUtils).checkUserHasNoPremium(userId);
        when(transactionService.buyItem(userId, PremiumPeriod.MONTHLY)).thenReturn(transaction);

        TransactionResultDto actual = premiumService.buyPremium(userId, premiumDuration);

        assertEquals(TransactionStatus.FAILED, actual.getStatus());
    }
}