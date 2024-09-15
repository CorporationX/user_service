package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {
    private User user;
    private PremiumPeriod premiumPeriod;
    private PaymentResponse paymentResponse;
    private Premium premium;
    private PremiumDto premiumDto;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumService premiumService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        premiumPeriod = PremiumPeriod.fromDays(30);
        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                67890L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment successful");
        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(user.getId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(3))
                .build();
        premium = new Premium();
    }

    @Test
    @DisplayName("Should successfully complete the premium purchase")
    void testBuyPremium_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(paymentResponse));
        when(premiumRepository.getPremiumPaymentNumber()).thenReturn(12345L);
        when(premiumMapper.toPremiumDto(any(Premium.class))).thenReturn(premiumDto);
        when(premiumRepository.save(any(Premium.class))).thenReturn(premium);

        PremiumDto result = premiumService.buyPremium(1L, premiumPeriod);

        assertNotNull(result);
        assertEquals(premiumDto, result);

        verify(userRepository).findById(1L);
        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toPremiumDto(any(Premium.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void testBuyPremium_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> premiumService.buyPremium(1L, premiumPeriod));

        verify(userRepository).findById(1L);
        verifyNoInteractions(paymentServiceClient, premiumRepository, premiumMapper);
    }

    @Test
    @DisplayName("Should throw exception if user already has an active premium subscription")
    void testBuyPremium_ExistingPremiumSubscription() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(premiumRepository.existsByUserId(anyLong())).thenReturn(true);

        Exception thrownException = assertThrows(ExistingPurchaseException.class, () -> {
            premiumService.buyPremium(1L, premiumPeriod);
        });

        assertEquals("User already has an active premium subscription", thrownException.getMessage());

        verify(userRepository).findById(1L);
        verify(premiumRepository).existsByUserId(1L);
        verifyNoMoreInteractions(paymentServiceClient, premiumMapper, premiumRepository);
    }
}
