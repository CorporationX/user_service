package school.faang.user_service.service.impl.premium;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.model.dto.premium.PaymentRequestDto;
import school.faang.user_service.model.dto.premium.PremiumDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.enums.premium.Currency;
import school.faang.user_service.model.entity.Premium;
import school.faang.user_service.model.enums.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PaymentServiceClient paymentServiceClient;
    private final PremiumRepository premiumRepository;
    private final UserService userService;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;

    @Value("${premium.batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        premiumValidator.validateUser(userId);
        var user = userService.findUserById(userId);
        var paymentRequestDto = buildRequest(period);
        var paymentResponseDto = paymentServiceClient.sendPayment(paymentRequestDto);
        premiumValidator.verifyPayment(paymentResponseDto);
        var premium = buildPremium(period, user);
        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    @Override
    @Transactional
    public void deleteExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        List<List<Premium>> expiredByBatches = ListUtils.partition(expiredPremiums, batchSize);

        expiredByBatches.forEach(this::deleteExpiredPremiumsByBatches);
    }

    @Override
    @Async("fixedThreadPools")
    public void deleteExpiredPremiumsByBatches(List<Premium> expiredPremiums) {
        premiumRepository.deleteAllInBatch(expiredPremiums);
    }

    private static PaymentRequestDto buildRequest(PremiumPeriod period) {
        return PaymentRequestDto.builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(period.getPrice())
                .currency(Currency.USD)
                .build();
    }

    private static Premium buildPremium(PremiumPeriod period, User user) {
        return Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }
}