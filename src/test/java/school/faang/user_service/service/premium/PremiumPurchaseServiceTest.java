package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumPurchaseServiceTest {

    private static final Long USER_ID = 1L;
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.ONE_MONTH;
    private static final long PAYMENT_NUMBER = 123456789L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @InjectMocks
    private PremiumPurchaseService premiumPurchaseService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private PremiumValidator premiumValidator;
    @Mock
    private PremiumMapper premiumMapper;
    @Mock
    private UserService userService;
    @Mock
    private PaymentService paymentService;

    private PremiumDto premiumDto;
    private User user;

    @BeforeEach
    void setUp() {
        premiumDto = PremiumDto.builder()
                .userId(USER_ID)
                .startDate(NOW)
                .endDate(NOW.plusDays(PREMIUM_PERIOD.getDays()))
                .build();
        user = User.builder().build();
    }

    @Nested
    class BuyPremiumTests {

        @Test
        @DisplayName("Successfully buy premium")
        void whenBuyPremiumThenSuccess() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(premiumRepository.getPremiumPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            when(premiumMapper.toPremiumDto(any(Premium.class))).thenReturn(premiumDto);

            PremiumDto result = premiumPurchaseService.buy(USER_ID, PREMIUM_PERIOD);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            verify(premiumValidator).validatePremiumAlreadyExistsByUserId(USER_ID);
            verify(paymentService).sendPayment(PREMIUM_PERIOD.getPrice(), PAYMENT_NUMBER);
            verify(premiumRepository).save(any(Premium.class));
        }

        @Test
        @DisplayName("Throws exception when payment fails")
        void whenPaymentFailsThenThrowException() {
            when(userService.getUserById(USER_ID)).thenReturn(user);
            when(premiumRepository.getPremiumPaymentNumber()).thenReturn(PAYMENT_NUMBER);
            doThrow(new RuntimeException("Payment failure")).when(paymentService).sendPayment(any(), any());

            assertThrows(RuntimeException.class, () -> premiumPurchaseService.buy(USER_ID, PREMIUM_PERIOD));
            verify(premiumRepository).save(any());
        }
    }
}
