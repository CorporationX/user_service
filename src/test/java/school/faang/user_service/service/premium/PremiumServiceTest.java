package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Captor
    private ArgumentCaptor<Premium> premiumArgumentCaptor;

    private final long userId = 1;
    private final PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;

    @Test
    public void testPremiumExistByUserId() {
        User user = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        when(premiumRepository.existsByUserId(userId))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> premiumService.buyPremium(userId, premiumPeriod));
    }

    @Test
    public void testPaymentFailed() {
        Pair<PaymentRequest, ResponseEntity<PaymentResponse>> paymentPair = setUpPaymentRequestAndResponse(false);
        User user = User.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(paymentPair.getSecond());

        assertThrows(PaymentFailedException.class,
                () -> premiumService.buyPremium(userId, premiumPeriod));
    }

    @Test
    public void testPremiumFindByUserId() {
        Pair<PaymentRequest, ResponseEntity<PaymentResponse>> paymentPair = setUpPaymentRequestAndResponse(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> premiumService.buyPremium(userId, premiumPeriod));
    }

    @Test
    public void testSavePremium() {
        Pair<PaymentRequest, ResponseEntity<PaymentResponse>> paymentPair = setUpPaymentRequestAndResponse(true);
        User user = User.builder()
                .id(1L)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(paymentPair.getSecond());

        premiumService.buyPremium(userId, premiumPeriod);

        verify(premiumRepository, times(1))
                .save(premiumArgumentCaptor.capture());
    }

    private Pair<PaymentRequest, ResponseEntity<PaymentResponse>> setUpPaymentRequestAndResponse(boolean isSuccessResponse) {
        long paymentNumber = 12345L;
        BigDecimal amount = BigDecimal.valueOf(99.99);
        Currency currency = Currency.USD;
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, currency);

        PaymentResponse paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                1234,
                paymentNumber,
                amount,
                currency,
                "Payment successful"
        );

        if (isSuccessResponse) {
            return Pair.of(paymentRequest, ResponseEntity.ok(paymentResponse));
        }
        return Pair.of(
                paymentRequest,
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(paymentResponse)
        );
    }
}
