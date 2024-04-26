package school.faang.user_service.service.premium;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.controller.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    @Mock
    PremiumRepository premiumRepository;
    @Mock
    PaymentServiceClient paymentServiceClient;
    @Mock
    UserRepository userRepository;
    @Mock
    PremiumMapper premiumMapper;
    @Mock
    ExecutorService executorService;
    @InjectMocks
    PremiumService premiumService;

    long userId;
    PremiumPeriod base;
    PremiumPeriod standard;
    PremiumPeriod legend;
    PaymentRequest paymentRequest;
    PaymentResponse paymentResponse;
    Premium premium;
    User user;

    @BeforeEach
    void setUp(){
        userId = 1;
        base = PremiumPeriod.BASE;
        standard = PremiumPeriod.STANDARD;
        legend = PremiumPeriod.LEGEND;

        paymentRequest = PaymentRequest.builder()
                .paymentNumber(UUID.randomUUID())
                .build();

        paymentResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .verificationCode(1)
                .paymentNumber(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.USD)
                .message("message")
                .build();

        user = User.builder()
                .id(1)
                .build();

        user = User.builder()
                .id(1)
                .build();

        premium = Premium.builder()
                .user(user)
                .build();
    }

    @Test
    @DisplayName("User already has premium subscription")
    public void testBuyPremium_UserAlreadyHasPremium(){
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        DataValidationException e = Assert.assertThrows(DataValidationException.class, () -> premiumService.buyPremium(userId, base));

        Assert.assertEquals(e.getMessage(), "The user " + userId + " already has Premium subscription");
    }
    @Test
    @DisplayName("Internal payment operation exception")
    public void testBuyPremium_InternalPaymentException(){
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPayment(Mockito.any(PaymentRequest.class))).thenReturn(ResponseEntity.badRequest().build());

        DataValidationException e = Assert.assertThrows(DataValidationException.class, () -> premiumService.buyPremium(userId, base));

        Assert.assertEquals(e.getMessage(), "Transaction failed!");
    }

    @Test
    @DisplayName("Successful buying premium")
    public void testBuy(){
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPayment(Mockito.any(PaymentRequest.class))).thenReturn(new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.OK));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(premiumRepository.save(Mockito.any(Premium.class))).thenReturn(premium);

        premiumService.buyPremium(1, base);
        Mockito.verify(premiumMapper, Mockito.times(1)).toDto(premium);
    }

    @Test
    @DisplayName("Successfully deletes expired premium access")
    public void testRemoveExpiredPremiums() {
        List<Premium> expiredPremiums = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Premium premium = new Premium();
            premium.setEndDate(LocalDateTime.now().minusDays(1));
            expiredPremiums.add(premium);
        }

        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(expiredPremiums);

        ReflectionTestUtils.setField(premiumService, "batchSize", 100);

        premiumService.removeExpiredPremiums();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(executorService).execute(any(Runnable.class));
    }
}