package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumAnalyticsDto;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumPaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumStatus;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.premium.PremiumAlreadyPurchasedException;
import school.faang.user_service.exception.premium.PremiumPaymentReplyNotReceivedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;
import school.faang.user_service.utils.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
import static school.faang.user_service.messages.ErrorMessages.PREMIUM_HAS_ALREADY_BEEN_PURCHASED;
import static school.faang.user_service.messages.ErrorMessages.UNABLE_TO_PAY_PREMIUM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBuyingService {
    private final UserRepository userRepository;
    private final JsonUtils jsonUtils;
    private final ReplyingKafkaTemplate<String, String, String> premiumPaymentReplyingKafkaTemplate;
    private final KafkaPublisher kafkaPublisher;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    @Value("${spring.kafka.producer.topics.premium.payment.payment-request-topic}")
    private String premiumPaymentRequestTopic;

    @Value("${spring.kafka.consumer.correlation.premium-payment}")
    private String premiumPaymentCorrelationId;

    @Value("${spring.kafka.producer.topics.premium.analytics.premium-analytics-topic}")
    private String premiumAnalyticsTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.auto-renew-failed-topic}")
    private String premiumAutoRenewFailedTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.payment-failed-topic}")
    private String premiumPaymentFailedTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.bought-topic}")
    private String premiumBoughtTopic;

    @Value("${spring.kafka.producer.topics.premium.notifications.updated-topic}")
    private String premiumUpdatedTopic;

    @Transactional(transactionManager = "kafkaTransactionManager")
    public PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto, boolean byUser) {
        User user = getUserById(premiumRequestDto.getUserId());
        if (user.getPremium() != null && user.getPremium().getEndDate().isBefore(LocalDateTime.now())) {
            log.error(PREMIUM_HAS_ALREADY_BEEN_PURCHASED.formatted(user.getId()));
            throw new PremiumAlreadyPurchasedException(PREMIUM_HAS_ALREADY_BEEN_PURCHASED.formatted(user.getId()));
        }
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(premiumRequestDto.getUserId(),
                premiumRequestDto.getPremiumType().getPriceInDollars(), premiumRequestDto.getSelectedCurrency());

        PremiumPaymentRequestDto premiumPaymentRequest = new PremiumPaymentRequestDto(
                premiumRequestDto, paymentRequestDto, CurrencyDto.USD, byUser);

        String correlationId = UUID.randomUUID().toString();
        String jsonRequest = jsonUtils.serialize(premiumPaymentRequest);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(premiumPaymentRequestTopic, jsonRequest);

        producerRecord.headers().add(new RecordHeader(premiumPaymentCorrelationId,
                correlationId.getBytes(StandardCharsets.UTF_8)));

        try {
            var replyFuture = premiumPaymentReplyingKafkaTemplate.sendAndReceive(producerRecord);
            log.info("Sent {} to topic {}", jsonRequest, premiumPaymentRequestTopic);
            PremiumPaymentResponseDto premiumPaymentResponse =
                    replyFuture.thenApply((ConsumerRecord<String, String> responseRecord) ->
                                    jsonUtils.deserialize(responseRecord.value(), PremiumPaymentResponseDto.class))
                            .join();
            return updatePremium(premiumPaymentResponse);
        } catch (CompletionException e) {
            log.error(NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE, e);
            throw new PremiumPaymentReplyNotReceivedException(NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE);
        }
    }

    private PremiumResponseDto updatePremium(PremiumPaymentResponseDto premiumPaymentResponse) {
        PaymentResponseDto paymentResponse = premiumPaymentResponse.getPaymentResponse();
        PremiumRequestDto premiumRequest = premiumPaymentResponse.getPremiumRequest();

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(premiumRequest.getPremiumType().getMonths());
        PremiumNotificationDto premiumNotification = createPremiumNotificationDto(premiumRequest, startDate, endDate);
        User user = getUserById(premiumRequest.getUserId());

        PremiumAnalyticsDto premiumAnalytics = PremiumAnalyticsDto.builder()
                .amount(premiumRequest.getPremiumType().getPriceInDollars())
                .currency(CurrencyDto.USD.toString())
                .country(user.getCountry().getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .premiumType(premiumRequest.getPremiumType())
                .userId(user.getId())
                .build();

        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            premiumAnalytics.setPremiumStatus(PremiumStatus.FAILED);
            sendPremiumAnalytics(premiumAnalytics);
            sendPremiumNotification(premiumNotification, premiumRequest.isAutoRenew() ?
                    premiumAutoRenewFailedTopic : premiumPaymentFailedTopic);

            log.error(UNABLE_TO_PAY_PREMIUM.formatted(premiumRequest.getUserId()));
            throw new PaymentFailedException(UNABLE_TO_PAY_PREMIUM.formatted(premiumRequest.getUserId()));
        }
        premiumAnalytics.setPremiumStatus(premiumPaymentResponse.isByUser() ?
                PremiumStatus.PURCHASED : PremiumStatus.REFUNDED);

        sendPremiumAnalytics(premiumAnalytics);
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(premiumRequest.isAutoRenew())
                .currency(premiumRequest.getSelectedCurrency())
                .build();

        sendPremiumNotification(premiumNotification, premiumPaymentResponse.isByUser() ?
                premiumBoughtTopic : premiumUpdatedTopic);

        premiumRepository.save(premium);
        PremiumResponseDto premiumResponse = premiumMapper.toPremiumResponseDto(premium);
        premiumResponse.setPremiumType(premiumRequest.getPremiumType());
        return premiumResponse;
    }

    private void sendPremiumNotification(PremiumNotificationDto premiumNotification, String topic) {
        kafkaPublisher.sendInTransaction(premiumNotification, topic);
    }

    private void sendPremiumAnalytics(PremiumAnalyticsDto premiumAnalyticsDto) {
        kafkaPublisher.sendInTransaction(premiumAnalyticsDto, premiumAnalyticsTopic);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with ID " + userId));
    }

    private PremiumNotificationDto createPremiumNotificationDto(
            PremiumRequestDto premiumRequest, LocalDateTime startDate, LocalDateTime endDate) {

        return PremiumNotificationDto.builder()
                .userId(premiumRequest.getUserId())
                .premiumType(premiumRequest.getPremiumType())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
