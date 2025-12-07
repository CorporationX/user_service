package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.client.dto.PaymentRequest;
import school.faang.user_service.client.dto.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.enums.PurchaseStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PremiumPurchaseAttemptRepository attemptRepository;
    private final PaymentServiceClient paymentClient;
    private final PremiumMapper premiumMapper;
    private final PremiumCacheService premiumCacheService;
    private final PremiumBoughtEventPublisher boughtEventPublisher;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        String paymentNumber = generatePaymentNumber(userId);

        PremiumPurchaseAttempt attempt = getOrCreateAttempt(userId, paymentNumber);

        if (isAttemptCompleted(attempt)) {
            log.info("Idempotent request detected for user: {}, paymentNumber: {}", userId, paymentNumber);
            return getExistingPremium(userId);
        }

        PaymentResponse response = null;
        if (attempt.getStatus() != PurchaseStatus.PAYMENT_SUCCESS) {
            response = processPayment(attempt, period, paymentNumber);
        }

        int verificationCode = response != null ? response.verificationCode() : 0;

        Premium premium = createOrExtendPremium(user, period, paymentNumber, verificationCode);

        markAttemptCompleted(attempt);

        publishPremiumBoughtEvent(userId, period, premium.getAmount(), LocalDateTime.now());

        return premiumMapper.toDto(premium);
    }

    @Transactional
    public void cancelPremium(long userId) {
        log.info("Starting premium cancellation for user: {}", userId);

        Premium premium = premiumRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Premium subscription not found for user: " + userId));

        LocalDateTime now = LocalDateTime.now();

        if (premium.getEndDate().isBefore(now)) {
            log.warn("Premium for user {} is already expired (endDate: {}), current time: {}",
                    userId, premium.getEndDate(), now);
            premiumCacheService.evict(userId);
            return;
        }

        LocalDateTime previousEndDate = premium.getEndDate();
        premium.setEndDate(now);

        Premium savedPremium = premiumRepository.save(premium);
        log.info("Premium cancelled for user: {}, previous end date: {}, new end date: {}",
                userId, previousEndDate, savedPremium.getEndDate());

        premiumCacheService.evict(userId);
        log.info("Premium cache evicted for user: {}", userId);
    }

    private PremiumPurchaseAttempt getOrCreateAttempt(long userId, String paymentNumber) {
        return attemptRepository.findByPaymentNumber(paymentNumber)
                .orElseGet(() -> {
                    PremiumPurchaseAttempt newAttempt = PremiumPurchaseAttempt.builder()
                            .userId(String.valueOf(userId))
                            .paymentNumber(paymentNumber)
                            .status(PurchaseStatus.PAYMENT_PENDING)
                            .build();

                    try {
                        return attemptRepository.save(newAttempt);
                    } catch (DataIntegrityViolationException e) {
                        log.warn("Failed to save attempt for paymentNumber: {}, retrying find", paymentNumber, e);
                        return attemptRepository.findByPaymentNumber(paymentNumber)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Failed to create or find attempt for payment: " + paymentNumber));
                    }
                });
    }

    private boolean isAttemptCompleted(PremiumPurchaseAttempt attempt) {
        return attempt.getStatus() == PurchaseStatus.COMPLETED
                || attempt.getStatus() == PurchaseStatus.PAYMENT_SUCCESS;
    }

    private PremiumDto getExistingPremium(long userId) {
        return premiumRepository.findFirstByUser_Id(userId)
                .map(premiumMapper::toDto)
                .orElseThrow(() -> new NotFoundException(
                        "Premium not found despite completed attempt for user: " + userId));
    }

    private PaymentResponse processPayment(PremiumPurchaseAttempt attempt, PremiumPeriod period, String paymentNumber) {
        BigDecimal amount = period.getAmount();
        Currency reqCurrency = Currency.USD;

        long numericPaymentNumber = Math.abs(UUID.nameUUIDFromBytes(paymentNumber.getBytes()).getMostSignificantBits());
        PaymentRequest request = new PaymentRequest(numericPaymentNumber, amount, reqCurrency);

        try {
            PaymentResponse response = paymentClient.processPayment(request);

            if (response == null  || response.status() != PaymentStatus.SUCCESS) {
                attempt.setStatus(PurchaseStatus.FAILED);
                attemptRepository.save(attempt);
                log.error("Payment failed for user: {}, paymentNumber: {}", attempt.getUserId(), paymentNumber);
                throw new PaymentFailedException("Payment failed for: " + paymentNumber);
            }

            attempt.setStatus(PurchaseStatus.PAYMENT_SUCCESS);
            attemptRepository.save(attempt);
            log.info("Payment successful for user: {}, verificationCode: {}",
                    attempt.getUserId(), response.verificationCode());
            return response;

        } catch (PaymentFailedException e) {
            throw e;
        } catch (Exception e) {
            attempt.setStatus(PurchaseStatus.FAILED);
            attemptRepository.save(attempt);
            log.error("Payment processing error for user: {}, paymentNumber: {}",
                    attempt.getUserId(), paymentNumber, e);
            throw new PaymentFailedException("Payment processing error: " + e.getMessage());
        }
    }

    private Premium createOrExtendPremium(User user, PremiumPeriod period,
                                          String paymentNumber, int verificationCode) {
        LocalDateTime now = LocalDateTime.now();

        return premiumRepository.findFirstByUser_IdAndEndDateAfter(user.getId(), now)
                .map(existing -> extendExistingPremium(existing, period, paymentNumber, verificationCode))
                .orElseGet(() -> createNewPremium(user, period, paymentNumber, verificationCode, now));
    }

    private Premium extendExistingPremium(Premium existing, PremiumPeriod period,
                                          String paymentNumber, int verificationCode) {
        LocalDateTime newEndDate = existing.getEndDate().plusMonths(period.getMonths());

        existing.setEndDate(newEndDate);
        existing.setPremiumPeriod(period);
        existing.setAmount(existing.getAmount().add(period.getAmount()));
        existing.setPaymentNumber(toLongPaymentNumber(paymentNumber));
        existing.setVerificationCode(verificationCode);
        existing.setCurrency(Currency.USD);

        Premium saved = premiumRepository.save(existing);
        log.info("Extended premium for user: {}, new end date: {}", existing.getUser().getId(), newEndDate);
        premiumCacheService.setActiveUntil(existing.getUser().getId(), newEndDate);
        return saved;
    }

    private Premium createNewPremium(User user, PremiumPeriod period, String paymentNumber,
                                     int verificationCode, LocalDateTime now) {
        LocalDateTime endDate = now.plusMonths(period.getMonths());

        Premium premium = Premium.builder()
                .user(user)
                .premiumPeriod(period)
                .startDate(now)
                .endDate(endDate)
                .amount(period.getAmount())
                .paymentNumber(toLongPaymentNumber(paymentNumber))
                .verificationCode(verificationCode)
                .currency(Currency.USD)
                .createdAt(now)
                .build();

        Premium saved = premiumRepository.save(premium);
        log.info("Created new premium for user: {}, end date: {}", user.getId(), endDate);
        premiumCacheService.setActiveUntil(user.getId(), endDate);
        return saved;
    }

    private void markAttemptCompleted(PremiumPurchaseAttempt attempt) {
        attempt.setStatus(PurchaseStatus.COMPLETED);
        attemptRepository.save(attempt);
        log.info("Premium purchase completed for user: {}, paymentNumber: {}",
                attempt.getUserId(), attempt.getPaymentNumber());
    }

    private String generatePaymentNumber(long userId) {
        return String.format("PREM-%d-%s", userId, UUID.randomUUID().toString().replace("-", ""));
    }

    private long toLongPaymentNumber(String paymentNumber) {
        return Math.abs(UUID.nameUUIDFromBytes(paymentNumber.getBytes()).getMostSignificantBits());
    }

    private void publishPremiumBoughtEvent(
            long userId,
            PremiumPeriod period,
            BigDecimal amount,
            LocalDateTime purchaseDateTime) {
        PremiumBoughtEvent event = PremiumBoughtEvent.builder()
                .userId(userId)
                .paymentAmount(amount)
                .subscriptionDurationMonths(period.getMonths())
                .purchaseDateTime(purchaseDateTime)
                .build();
        boughtEventPublisher.publish(event);
    }
}