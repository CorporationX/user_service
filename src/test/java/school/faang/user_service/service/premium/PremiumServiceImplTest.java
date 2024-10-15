package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.model.dto.premium.PaymentResponseDto;
import school.faang.user_service.model.dto.premium.PremiumDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.premium.Currency;
import school.faang.user_service.model.entity.premium.PaymentStatus;
import school.faang.user_service.model.entity.premium.Premium;
import school.faang.user_service.model.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.impl.premium.PremiumServiceImpl;
import school.faang.user_service.service.impl.user.UserServiceImpl;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    private final long userId = 1L;
    private User user;
    private PremiumPeriod period;
    private PaymentResponseDto paymentResponse;
    private Premium premium;
    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "batchSize", 1);
        period = PremiumPeriod.MONTH;
    }

    @Test
    void buyPremium_whenValidPayment_thenSaveUser() {
        // given
        user = buildUser();
        when(userService.findUserById(userId)).thenReturn(user);
        paymentResponse = buildPaymentResponse();
        when(paymentServiceClient.sendPayment(any())).thenReturn(paymentResponse);
        premium = buildPremium();
        when(premiumRepository.save(any())).thenReturn(premium);
        premiumDto = buildPremiumDto();
        when(premiumMapper.toDto(premium)).thenReturn(premiumDto);
        // when
        premiumService.buyPremium(userId, period);
        // then
        verify(premiumValidator).validateUser(userId);
        verify(userService).findUserById(userId);
        verify(paymentServiceClient).sendPayment(any());
        verify(premiumValidator).verifyPayment(paymentResponse);
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(premium);
    }

    @Test
    void testDeleteExpiredPremiums() {
        List<Premium> expiredPremiums = List.of(Premium.builder()
                .endDate(LocalDateTime.now().minusDays(3))
                .build(), buildPremium());
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(expiredPremiums);

        premiumService.deleteExpiredPremiums();

        verify(premiumRepository, times(2)).deleteAllInBatch(any());
    }

    private User buildUser() {
        return User.builder()
                .id(userId)
                .username("testUser")
                .build();
    }

    private PaymentResponseDto buildPaymentResponse() {
        return PaymentResponseDto.builder()
                .status(PaymentStatus.SUCCESS)
                .paymentNumber(1L)
                .amount(period.getPrice())
                .currency(Currency.USD)
                .message("Payment successful")
                .build();
    }

    private Premium buildPremium() {
        return Premium.builder()
                .id(1L)
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }

    private PremiumDto buildPremiumDto() {
        return PremiumDto.builder()
                .id(premium.getId())
                .userId(userId)
                .startDate(premium.getStartDate())
                .endDate(premium.getEndDate())
                .build();
    }
}