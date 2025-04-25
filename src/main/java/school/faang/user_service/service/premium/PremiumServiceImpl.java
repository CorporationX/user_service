package school.faang.user_service.service.premium;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.premium.PremiumNotActiveException;
import school.faang.user_service.exception.premium.PremiumPriceReplyNotReceivedException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.utils.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static school.faang.user_service.messages.ErrorMessages.NO_ACTIVE_PREMIUM;
import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final UserRepository userRepository;
    private final ReplyingKafkaTemplate<String, String, String> premiumPriceReplyingKafkaTemplate;
    private final JsonUtils jsonUtils;
    private final PremiumRenewalService premiumRenewalService;
    private ExecutorService executor;
    private final PremiumBuyingService premiumBuyingService;

    @Value("${app.premium.renew-batch-size}")
    private int renewBatchSize;

    @Value("${app.premium.thread-pool-size}")
    private int renewThreadPoolSize;

    @Value("${app.premium.timeout-hours}")
    private int premiumRenewalTimeoutHours;

    @Value("${spring.kafka.producer.topics.premium.payment.payment-request-topic}")
    private String premiumPaymentRequestTopic;

    @Value("${spring.kafka.producer.topics.premium.payment.price-request-topic}")
    private String premiumPriceRequestTopic;

    @Value("${spring.kafka.consumer.correlation.premium-price}")
    private String premiumPriceCorrelationId;

    @PostConstruct
    public void setUp() {
        executor = Executors.newFixedThreadPool(renewThreadPoolSize);
    }

    @Override
    public PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto, boolean byUser) {
        return premiumBuyingService.buyPremium(premiumRequestDto, byUser);
    }

    @Override
    @Transactional(transactionManager = "kafkaTransactionManager")
    public ExchangeResponseDto getPremiumPrice(PremiumRequestDto premiumRequestDto) {
        ExchangeRequestDto exchangeRequest = ExchangeRequestDto.builder()
                .fromCurrency(CurrencyDto.USD)
                .toCurrency(premiumRequestDto.getSelectedCurrency())
                .amount(premiumRequestDto.getPremiumType().getPriceInDollars())
                .userId(premiumRequestDto.getUserId())
                .build();

        String correlationId = UUID.randomUUID().toString();
        String jsonRequest = jsonUtils.serialize(exchangeRequest);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(premiumPriceRequestTopic, jsonRequest);

        producerRecord.headers().add(new RecordHeader(premiumPriceCorrelationId,
                correlationId.getBytes(StandardCharsets.UTF_8)));

        try {
            var replyFuture = premiumPriceReplyingKafkaTemplate.sendAndReceive(producerRecord);
            log.info("Sent {} to topic {}", jsonRequest, premiumPaymentRequestTopic);
            return replyFuture.thenApply((ConsumerRecord<String, String> responseRecord) ->
                            jsonUtils.deserialize(responseRecord.value(), ExchangeResponseDto.class))
                    .join();
        } catch (CompletionException e) {
            log.error(NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE, e);
            throw new PremiumPriceReplyNotReceivedException(NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE);
        }
    }

    @Override
    @Transactional
    public void updateAutoRenew(boolean autoRenew, Long userId) {
        User user = getUserById(userId);
        if (user.getPremium() == null) {
            String message = NO_ACTIVE_PREMIUM.formatted(userId);
            log.error(message);
            throw new PremiumNotActiveException(message);
        }
        user.getPremium().setAutoRenew(autoRenew);
    }

    @Override
    public void premiumRenewal() {
        log.info("Premium renew process started");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int userCount = (int) userRepository.count();

        int batches = (userCount + renewBatchSize - 1) / renewBatchSize;
        IntStream.range(0, batches).forEach(batchNumber -> {
            Pageable pageable = PageRequest.of(batchNumber, renewBatchSize);
            futures.add(CompletableFuture.runAsync(() -> {
                premiumRenewalService.updatePremiumForUsers(
                        userRepository.findPremiumActiveUsers(pageable).getContent());
            }, executor));
        });

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(premiumRenewalTimeoutHours, TimeUnit.HOURS);
            log.info("Premium renew process completed");
        } catch (ExecutionException e) {
            log.error("Execution exception while renewing premium", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while renewing premium", e);
            executor.shutdownNow();
            log.warn("Premium renew process was interrupted and may not have completed");
        } catch (TimeoutException e) {
            log.error("Premium renew process didi not complete within {} hours", premiumRenewalTimeoutHours);
        } finally {
            executor.shutdown();
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId));
    }
}
