package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.scheduler.PremiumRemoverTransactions;
import school.faang.user_service.service.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumServiceImpl implements PremiumService {

    @Value("${premium.removal.batch-size}")
    private int batchSize;

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;
    private final PremiumRemoverTransactions premiumRemoverTransactions;

    @Override
    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (premiumRepository.existsByUserId(userId)) {
            throw new ExistingPurchaseException("User already has an active premium subscription");
        }

        payPremium(premiumPeriod, user);

        Premium savedPremium = savePremium(premiumPeriod, user);
        return premiumMapper.toPremiumDto(savedPremium);
    }

    private void payPremium(PremiumPeriod premiumPeriod, User user) {
        long paymentNumber = premiumRepository.getPremiumPaymentNumber();
        PaymentRequest paymentRequest =
                new PaymentRequest(paymentNumber, new BigDecimal(premiumPeriod.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse response = paymentResponseEntity.getBody();

        if (response == null) {
            log.error("No response received from payment service for user {}.", user.getUsername());
            throw new PaymentFailureException("No response from payment service.");
        }

        if (response.status() == PaymentStatus.FAIL) {
            log.error("Payment failed for user {} for the period of {} days.",
                    user.getUsername(), premiumPeriod.getDays());
            throw new PaymentFailureException(String.format("Failure to effect premium payment" +
            " for user %s for requested period of %d days.", user.getUsername(), premiumPeriod.getDays()));
        }

        log.info("Payment response received for user {}: {}", user.getUsername(), response.message());
    }

    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        Premium premium = new Premium();
        premium.setUser(user);
        LocalDateTime startDate = LocalDateTime.now();
        premium.setStartDate(startDate);
        LocalDateTime endDate = startDate.plusDays(premiumPeriod.getDays());
        premium.setEndDate(endDate);
        return premiumRepository.save(premium);
    }

    @Override
    public List<List<Long>> findAndSplitExpiredPremiums() {
        List<Long> expiredPremiumsIds = premiumRepository.findAllByEndDateBefore(LocalDateTime.now()).stream()
                .map(Premium::getId).toList();

        return splitIntoBatches(expiredPremiumsIds, batchSize);
    }

    @Override
    @Async("premiumRemovalAsyncExecutor")
    public CompletableFuture<Integer> deleteExpiredPremiumsByIds(List<Long> ids) {
        return CompletableFuture.supplyAsync(() -> premiumRemoverTransactions.deletePremiums(ids));
    }

    private <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }
}
