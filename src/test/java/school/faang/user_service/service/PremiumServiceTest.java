package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.mapper.PremiumMapperImpl;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Spy
    private PremiumMapper premiumMapper = new PremiumMapperImpl();

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserService userService;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private PremiumService premiumService;

    @Test
    void buyPremium_whenUserNotHavePremium_thenCreatePremium() {
        long userId = 1L;
        PremiumPeriod premiumPeriod = PremiumPeriod.ONE_MONTH;

        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1, 1, null, null, null);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        PremiumDto result = premiumService.buyPremium(userId, premiumPeriod);

        assertNotNull(result);
        verify(premiumRepository).save(any(Premium.class));
    }

    @Test
    void buyPremium_whenUserAlreadyHavePremium_thenThrowException() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> premiumService.buyPremium(userId, PremiumPeriod.ONE_MONTH));

        assertEquals("Пользователь уже имеет премиум подписку", dataValidationException.getMessage());
    }

    @Test
    void buyPremium_whenPaymentFailed_thenThrowException() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.FAILURE, 1, 1, null, null, null);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> premiumService.buyPremium(userId, PremiumPeriod.ONE_MONTH));

        assertEquals("Ошибка платежа", dataValidationException.getMessage());
    }
}
