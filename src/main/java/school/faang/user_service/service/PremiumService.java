package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.PaymentPayException;
import school.faang.user_service.exception.PaymentServiceException;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.kafka.events.PremiumBoughtEvent;
import school.faang.user_service.kafka.producer.PremiumBoughtEventProducer;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final static String CURRENCY = "USD";
    private final static int MAX_PAYMENT_NUMBER = 10000;
    private final static int MIN_PAYMENT_NUMBER = 1000;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserService userService;
    private final PremiumMapper premiumMapper;
    private final PremiumBoughtEventProducer premiumBoughtEventProducer;

    @Value("${app.config.max_data_group_size}")
    private int removerBatchSize;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        existsByUserId(userId);

        var user = userService.getUserById(userId);
        int paymentNumber = new Random().nextInt(MIN_PAYMENT_NUMBER, MAX_PAYMENT_NUMBER);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .currency(Currency.getInstance(CURRENCY))
                .paymentNumber(paymentNumber)
                .amount(premiumPeriod.getPrice())
                .build();

        return payToPremium(user, paymentRequest, premiumPeriod);
    }

    private PremiumDto payToPremium(User user, PaymentRequest paymentRequest, PremiumPeriod premiumPeriod) {
        try {
            PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);
            if (paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error("Payment failed. {} | {} | {}", user, paymentResponse, premiumPeriod);
                throw new PaymentPayException("Payment failed.");
            }

            Premium premium = Premium.builder()
                    .user(user)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()))
                    .build();

            premiumRepository.save(premium);
            log.info("Premium bought. {} | {} | {}", user, paymentResponse, premiumPeriod);

            PremiumBoughtEvent premiumBoughtEvent = PremiumBoughtEvent.builder()
                    .userId(user.getId())
                    .amount(paymentRequest.amount().doubleValue())
                    .duration(premiumPeriod.getDays())
                    .boughtAt(LocalDateTime.now())
                    .build();

            premiumBoughtEventProducer.sendEvent(premiumBoughtEvent);

            return premiumMapper.toDto(premium);
        } catch (PaymentServiceException e) {
            log.error("Payment service not working. {}", e.getMessage());
            throw new PaymentServiceException("Payment service not working.");
        }
    }

    public void existsByUserId(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new PremiumAlreadyExistsException("The user is already available in the premium");
        }
    }

    public void removeExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expiredPremiums.isEmpty()) {
            log.info("[{}] No expired premiums to remove.", LocalDateTime.now());
        } else {
            log.info("[{}] Found {} expired premiums to remove.", LocalDateTime.now(), expiredPremiums.size());

            List<List<Premium>> expiredBatches = ListUtils.partition(expiredPremiums, removerBatchSize);
            expiredBatches.forEach(this::removeExpiredPremiumsByBatches);
        }
    }

    @Async("executorService")
    public void removeExpiredPremiumsByBatches(List<Premium> expiredBatches) {
        if (expiredBatches.isEmpty()) {
            log.info("[{}] Expired batch is empty.", LocalDateTime.now());
        } else {
            premiumRepository.deleteAll(expiredBatches);
            log.info("[{}] Removed {} expired premiums.", LocalDateTime.now(), expiredBatches.size());
        }
    }
}
