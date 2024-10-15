package school.faang.user_service.validator.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.premium.PaymentResponseDto;
import school.faang.user_service.model.entity.premium.PaymentStatus;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumValidatorTest {

    @InjectMocks
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumRepository premiumRepository;

    private final long userId = 1L;
    private PaymentResponseDto paymentResponseDto;

    @Test
    void validateUser_whenUserDoesNotHavePremium_thenCorrect() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        assertDoesNotThrow(() -> premiumValidator.validateUser(userId));
    }

    @Test
    void validateUser_whenUserHasPremium_thenThrowException() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> premiumValidator.validateUser(userId),
                "User with ID: %d already has premium access.".formatted(userId));
    }

    @Test
    void verifyPayment_whenPaymentSuccessful_thenCorrect() {
        paymentResponseDto = PaymentResponseDto.builder()
                .status(PaymentStatus.SUCCESS)
                .paymentNumber(1L)
                .build();
        assertDoesNotThrow(() -> premiumValidator.verifyPayment(paymentResponseDto));
    }

    @Test
    void verifyPayment_whenPaymentFailed_thenThrowException() {
        paymentResponseDto = PaymentResponseDto.builder()
                .status(null)
                .paymentNumber(1L)
                .build();
        assertThrows(IllegalStateException.class, () -> premiumValidator.verifyPayment(paymentResponseDto),
                "Payment with payment number: %d failed.".formatted(paymentResponseDto.paymentNumber()));
    }
}