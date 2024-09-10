package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    private final long userId = 1L;
    private User user;
    private PremiumPeriod period;
    private PaymentResponse paymentResponse;
    private Premium premium;
    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        period = PremiumPeriod.MONTH;
    }

    @Test
    void buyPremium_whenValidPayment_thenSaveUser() {
        // given
        user = buildUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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
        verify(userRepository).findById(userId);
        verify(paymentServiceClient).sendPayment(any());
        verify(premiumValidator).verifyPayment(paymentResponse);
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(premium);
    }

    @Test
    void buyPremium_whenUserNotFound_thenThrowException() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when/then
        assertThrows(IllegalStateException.class, () -> premiumService.buyPremium(userId, period),
                "User with ID: %d does not exist.".formatted(userId));
    }

    private User buildUser() {
        return User.builder()
                .id(userId)
                .username("testUser")
                .build();
    }

    private PaymentResponse buildPaymentResponse() {
        return PaymentResponse.builder()
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