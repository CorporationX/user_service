package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.PaymentException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserService userService;

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumService premiumService;

    private PremiumPeriod period;
    private Long userId;
    private User user;
    private Premium premium;
    private PremiumDto premiumDto;

    @BeforeEach
    public void setUp() {
        period = PremiumPeriod.ONE_MONTH;
        userId = 1L;
        user = new User();
        user.setId(userId);
        premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(userId)
                .startDate(premium.getStartDate())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }

    @Test
    public void testBuyPremium_Success() {
        // arrange
        when(premiumRepository.existsById(userId)).thenReturn(false);
        when(userService.getUserById(userId)).thenReturn(user);
        when(paymentServiceClient.sendPaymentRequest(
                new PaymentRequestDto(userId, period.getDays())
        ))
                .thenReturn(true);
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(premiumDto);

        // act
        PremiumDto result = premiumService.buyPremium(period, userId);

        // assert
        verify(premiumRepository).save(any(Premium.class));
        assertEquals(premiumDto, result);
    }

    @Test
    public void testBuyPremium_UserAlreadyHasPremium() {
        // arrange
        when(premiumRepository.existsById(userId)).thenReturn(true);

        // act & assert
        assertThrows(DataValidationException.class, () -> premiumService.buyPremium(period, userId));
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    public void testBuyPremium_PaymentFailed() {
        // arrange
        when(premiumRepository.existsById(userId)).thenReturn(false);
        when(userService.getUserById(userId)).thenReturn(user);
        when(paymentServiceClient.sendPaymentRequest(
                new PaymentRequestDto(userId, period.getDays())
        ))
                .thenReturn(false);

        // act & assert
        assertThrows(PaymentException.class, () -> premiumService.buyPremium(period, userId));
        verify(premiumRepository, never()).save(any(Premium.class));
    }
}
