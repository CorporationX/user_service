package school.faang.user_service.service.service.premium;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.exchange.ExchangeRequestDto;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.premium.PremiumType;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.premium.PremiumNotActiveException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.premium.PremiumRenewalService;
import school.faang.user_service.service.premium.PremiumServiceImpl;
import school.faang.user_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.messages.ErrorMessages.NO_ACTIVE_PREMIUM;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @InjectMocks
    private PremiumServiceImpl premiumService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
    private RequestReplyFuture<String, String, String> requestReplyFuture;

    @Mock
    private PremiumRenewalService premiumRenewalService;

    private final Country country = new Country(1L, "Title", Collections.emptyList());
    private PremiumRequestDto premiumRequestDto;
    private User user;
    private ExchangeResponseDto exchangeResponse;
    private ConsumerRecord<String, String> consumerRecord;

    private final String jsonRequest = "jsonRequest";
    private final String jsonResponse = "jsonResponse";
    private final String premiumExpiredTopic = "topic2";
    private final String premiumPriceRequestTopic = "topic9";
    private final String premiumPriceCorrelationId = "correlation-id-1";

    @BeforeEach
    public void setUp() {
        premiumRequestDto = new PremiumRequestDto(PremiumType.ONE_MONTH, 1L, CurrencyDto.USD, true);
        user = User.builder().id(1L).username("name").country(country).build();
        consumerRecord = new ConsumerRecord<>("topic", 0, 0L, "key", jsonResponse);
        exchangeResponse = new ExchangeResponseDto(CurrencyDto.USD, BigDecimal.TEN, 1L);
        requestReplyFuture = new RequestReplyFuture<>();
        requestReplyFuture.complete(consumerRecord);

        ReflectionTestUtils.setField(premiumService, "premiumPriceRequestTopic", premiumPriceRequestTopic);
        ReflectionTestUtils.setField(premiumRenewalService, "premiumExpiredTopic", premiumExpiredTopic);

        ReflectionTestUtils.setField(premiumService, "premiumPriceCorrelationId", premiumPriceCorrelationId);

        ReflectionTestUtils.setField(premiumService, "renewThreadPoolSize", 2);
        ReflectionTestUtils.setField(premiumService, "renewBatchSize", 100);
        ReflectionTestUtils.setField(premiumService, "premiumRenewalTimeoutHours", 1);

        premiumService.setUp();
    }

    @Test
    public void testGetPremiumPrice_success() {
        when(jsonUtils.serialize(any(ExchangeRequestDto.class))).thenReturn(jsonRequest);
        when(jsonUtils.deserialize(anyString(), eq(ExchangeResponseDto.class)))
                .thenReturn(exchangeResponse);

        when(replyingKafkaTemplate.sendAndReceive(Mockito.<ProducerRecord<String, String>>any()))
                .thenReturn(requestReplyFuture);

        premiumService.getPremiumPrice(premiumRequestDto);

        verify(jsonUtils, times(1)).serialize(any(ExchangeRequestDto.class));
        verify(jsonUtils, times(1)).deserialize(jsonResponse, ExchangeResponseDto.class);
    }

    @Test
    public void testUpdateAutoRenew_userNotFound() {
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> premiumService.updateAutoRenew(true, premiumRequestDto.getUserId())
        );

        assertEquals("No user with ID " + premiumRequestDto.getUserId(), exception.getMessage());
    }

    @Test
    public void testUpdateAutoRenew_noActivePremium() {
        user.setPremium(null);
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        PremiumNotActiveException exception = assertThrows(PremiumNotActiveException.class,
                () -> premiumService.updateAutoRenew(true, premiumRequestDto.getUserId())
        );

        assertEquals(NO_ACTIVE_PREMIUM.formatted(premiumRequestDto.getUserId()), exception.getMessage());
    }

    @Test
    public void testUpdateAutoRenew_success() {
        user.setPremium(new Premium());
        when(userRepository.findById(premiumRequestDto.getUserId())).thenReturn(Optional.of(user));

        premiumService.updateAutoRenew(true, premiumRequestDto.getUserId());

        assertTrue(user.getPremium().isAutoRenew());
    }

    @Test
    public void testPremiumRenewal() {
        when(userRepository.count()).thenReturn(1L);
        List<User> users = new ArrayList<>();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .endDate(LocalDateTime.now().plusDays(1))
                .autoRenew(false)
                .build();

        user.setPremium(premium);
        users.add(user);
        when(userRepository.findPremiumActiveUsers(any(Pageable.class))).thenReturn(new PageImpl<>(users));

        premiumService.premiumRenewal();

        verify(premiumRenewalService, times(1)).updatePremiumForUsers(users);
    }
}
