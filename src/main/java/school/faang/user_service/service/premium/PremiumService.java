package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.service.transaction.TransactionService;
import school.faang.user_service.service.utils.PremiumServiceUtils;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumServiceUtils premiumServiceUtils;
    private final TransactionService transactionService;

    public TransactionResultDto buyPremium(Long userId, Integer durationDays) {
        premiumServiceUtils.checkUserHasNoPremium(userId);
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(durationDays);
        TransactionResultDto transactionResultDto = transactionService.buyItem(userId, premiumPeriod);
        if (transactionResultDto.getStatus()
                .equals(TransactionStatus.SETTLED)) {
            premiumServiceUtils.assignPremiumToUser(userId, durationDays);
        }
        return transactionResultDto;
    }
}
