package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.*;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.premium.PremiumValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PremiumValidator premiumValidator;
    @Mock
    private PremiumMapper premiumMapper;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private UserValidator userValidator;

    private PremiumDto premiumDto;
    private PaymentResponse paymentResponse;
    private Premium premium;
    private static final Long USER_ID = 1L;
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.ONE_MONTH;
    private static final long PAYMENT_NUMBER = 123456789L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(PREMIUM_PERIOD.getPrice());
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        premiumDto = new PremiumDto(USER_ID, NOW, NOW.plusDays(PREMIUM_PERIOD.getDays()));
        premium = new Premium();
        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                PAYMENT_NUMBER,
                AMOUNT,
                Currency.USD,
                Currency.EUR,
                "Payment successful"
        );
    }

    @Nested
    class BuyPremiumTests {

        @Test
        @DisplayName("Successfully buy premium")
        void whenBuyPremiumThenSuccess() {
            when(premiumRepository.getPremiumPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(paymentResponse));
            when(premiumMapper.toEntity(any(PremiumDto.class))).thenReturn(premium);

            PremiumDto result = premiumService.buy(USER_ID, PREMIUM_PERIOD);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            verify(userValidator).validateUserIsExisted(USER_ID);
            verify(premiumValidator).validatePremiumAlreadyExistsByUserId(USER_ID);
            verify(premiumRepository).save(any(Premium.class));
        }

        @Test
        @DisplayName("Throws exception when no response from payment service")
        void whenNoPaymentResponseThenThrowException() {
            when(premiumRepository.getPremiumPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.of(Optional.empty()));

            assertThrows(PaymentFailureException.class, () -> premiumService.buy(USER_ID, PREMIUM_PERIOD));
            verify(premiumRepository, never()).save(any());
        }

        @Test
        @DisplayName("Throws exception when payment fails")
        void whenPaymentFailureThenThrowException() {
            PaymentResponse failedPaymentResponse = new PaymentResponse(
                    PaymentStatus.FAILURE,
                    12345,
                    PAYMENT_NUMBER,
                    AMOUNT,
                    Currency.USD,
                    Currency.EUR,
                    "Payment failed"
            );
            when(premiumRepository.getPremiumPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(failedPaymentResponse));

            assertThrows(PaymentFailureException.class, () -> premiumService.buy(USER_ID, PREMIUM_PERIOD));
            verify(premiumRepository, never()).save(any());
        }
    }
}
