package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PremiumServiceTest {
    @InjectMocks
    private PremiumService premiumService;
    @Mock
    private PremiumRepository premiumRepository;
    @Mock
    private UserService userService;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PremiumMapper premiumMapper;
    private PremiumPeriod premiumPeriod;
    private Long userId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "random", new Random());
        premiumPeriod = PremiumPeriod.fromDays(30);
        userId = 1L;
        User user = User.builder().id(userId).build();
        PaymentResponse paymentResponse = PaymentResponse.builder().status(PaymentStatus.SUCCESS).build();
        ResponseEntity<PaymentResponse> responseEntity = ResponseEntity.ok(paymentResponse);
        Premium premium = mock(Premium.class);

        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(userService.getUser(userId)).thenReturn(User.builder().id(userId).build());
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(responseEntity);
        when(userService.getUser(userId)).thenReturn(user);
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);
    }

    @Test
    void buyPremium_shouldInvokeRepositoryExistsByUserId() {
        premiumService.buyPremium(userId, premiumPeriod);
        verify(premiumRepository).existsByUserId(userId);
    }

    @Test
    void buyPremium_shouldInvokePaymentServiceClientSendPayment() {
        premiumService.buyPremium(userId, premiumPeriod);
        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class));
    }

    @Test
    void buyPremium_shouldInvokeUserServiceGetUser() {
        premiumService.buyPremium(userId, premiumPeriod);
        verify(userService).getUser(userId);
    }

    @Test
    void buyPremium_shouldInvokePremiumRepositorySave() {
        premiumService.buyPremium(userId, premiumPeriod);
        verify(premiumRepository).save(any(Premium.class));
    }

    @Test
    void buyPremium_shouldInvokePremiumMapperToDto() {
        premiumService.buyPremium(userId, premiumPeriod);
        verify(premiumMapper).toDto(any(Premium.class));
    }

    @Test
    void buyPremium_shouldThrowDataValidationException() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> premiumService.buyPremium(userId, premiumPeriod),
                "User with id " + userId + " already has premium");
    }

    @Test
    void buyPremium_shouldThrowPaymentFailedException() {
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        assertThrows(PaymentFailedException.class,
                () -> premiumService.buyPremium(userId, premiumPeriod),
                "Payment failed. Please try again");
    }
}