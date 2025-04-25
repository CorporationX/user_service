package school.faang.user_service.service.service.premium;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumAnalyticsDto;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumPaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.premium.PremiumAlreadyPurchasedException;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;
import school.faang.user_service.service.premium.PremiumBuyingService;
import school.faang.user_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.messages.ErrorMessages.PREMIUM_HAS_ALREADY_BEEN_PURCHASED;

@ExtendWith(MockitoExtension.class)
public class PremiumBuyingServiceTest {

    @InjectMocks
    private PremiumBuyingService premiumBuyingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
    private RequestReplyFuture<String, String, String> requestReplyFuture;

    private final Country country = new Country(1L, "Title", Collections.emptyList());
    private PremiumRequestDto premiumRequestDto;
    private User user;
    private PremiumPaymentResponseDto premiumPaymentResponse;
    private PaymentResponseDto paymentResponseDto;
    private ConsumerRecord<String, String> consumerRecord;

    private final String jsonRequest = "jsonRequest";
    private final String jsonResponse = "jsonResponse";
    private final String premiumBoughtTopic = "topic1";
    private final String premiumAutoRenewFailedTopic = "topic4";
    private final String premiumUpdatedTopic = "topic5";
    private final String premiumAnalyticsTopic = "topic6";
    private final String premiumPaymentFailedTopic = "topic7";
    private final String premiumPaymentRequestTopic = "topic8";
    private final String premiumPaymentCorrelationId = "correlation-id-2";

    @BeforeEach
    public void setUp() {
        paymentResponseDto = new PaymentResponseDto(PaymentStatus.SUCCESS, 1, 1L,
                BigDecimal.TEN, CurrencyDto.USD, "message");
        premiumRequestDto = new PremiumRequestDto(PremiumType.ONE_MONTH, 1L, CurrencyDto.USD, true);
        user = User.builder().id(1L).username("name").country(country).build();
        premiumPaymentResponse = new PremiumPaymentResponseDto(premiumRequestDto, paymentResponseDto, true);
        consumerRecord = new ConsumerRecord<>("topic", 0, 0L, "key", jsonResponse);
        requestReplyFuture = new RequestReplyFuture<>();
        requestReplyFuture.complete(consumerRecord);

        ReflectionTestUtils.setField(premiumBuyingService, "premiumPaymentRequestTopic", premiumPaymentRequestTopic);
        ReflectionTestUtils.setField(premiumBuyingService, "premiumAnalyticsTopic", premiumAnalyticsTopic);
        ReflectionTestUtils.setField(premiumBuyingService, "premiumAutoRenewFailedTopic", premiumAutoRenewFailedTopic);
        ReflectionTestUtils.setField(premiumBuyingService, "premiumPaymentFailedTopic", premiumPaymentFailedTopic);
        ReflectionTestUtils.setField(premiumBuyingService, "premiumBoughtTopic", premiumBoughtTopic);
        ReflectionTestUtils.setField(premiumBuyingService, "premiumUpdatedTopic", premiumUpdatedTopic);

        ReflectionTestUtils.setField(premiumBuyingService, "premiumPaymentCorrelationId", premiumPaymentCorrelationId);
    }

    @Test
    public void testBuyPremium_purchasedByUser() {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(jsonUtils.serialize(any(PremiumPaymentRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(PremiumPaymentResponseDto.class)))
                .thenReturn(premiumPaymentResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumBuyingService.buyPremium(premiumRequestDto, true);

        verify(jsonUtils, times(1)).serialize(any(PremiumPaymentRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, PremiumPaymentResponseDto.class);
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumAnalyticsDto.class), eq(premiumAnalyticsTopic));
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumBoughtTopic));
        verify(premiumRepository, times(1)).save(any(Premium.class));
    }

    @Test
    public void testBuyPremium_userAlreadyHasPremium() {
        user.setPremium(Premium.builder().endDate(LocalDateTime.now().minusDays(1)).build());
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        PremiumAlreadyPurchasedException exception = assertThrows(PremiumAlreadyPurchasedException.class,
                () -> premiumBuyingService.buyPremium(premiumRequestDto, true)
        );

        assertEquals(PREMIUM_HAS_ALREADY_BEEN_PURCHASED.formatted(user.getId()), exception.getMessage());
    }

    @Test
    public void testBuyPremium_userNotFound() {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> premiumBuyingService.buyPremium(premiumRequestDto, true)
        );

        assertEquals("No user with ID " + premiumRequestDto.getUserId(), exception.getMessage());
    }

    @Test
    public void testBuyPremium_purchasedAutomatically() {
        premiumPaymentResponse.setByUser(false);
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));
        when(jsonUtils.serialize(any(PremiumPaymentRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(PremiumPaymentResponseDto.class)))
                .thenReturn(premiumPaymentResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumBuyingService.buyPremium(premiumRequestDto, false);

        verify(jsonUtils, times(1)).serialize(any(PremiumPaymentRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, PremiumPaymentResponseDto.class);
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumAnalyticsDto.class), eq(premiumAnalyticsTopic));
        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumUpdatedTopic));
        verify(premiumRepository, times(1)).save(any(Premium.class));
    }
}
