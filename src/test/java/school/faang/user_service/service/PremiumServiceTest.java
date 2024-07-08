package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.exception.PremiumAlreadyPurchasedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumMapper premiumMapper;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    private long userId;
    private User user;
    private PremiumDto premiumDto;
    private PremiumPeriod premiumPeriod;
    private PaymentRequest paymentRequest;
    private PaymentResponse successPaymentResponse;
    private PaymentResponse errorPaymentResponse;

    private ArgumentCaptor<Premium> premiumArgumentCaptor;

    @BeforeEach
    void setUp() {
        userId = 0;
        user = new User();
        premiumDto = new PremiumDto(
            null,
            userId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        premiumPeriod = PremiumPeriod.MONTHLY;
        paymentRequest = new PaymentRequest(
            0,
            new BigDecimal(premiumPeriod.getCost()),
            Currency.USD
        );
        successPaymentResponse = new PaymentResponse(
            PaymentStatus.SUCCESS,
            0,
            0,
            new BigDecimal(premiumPeriod.getCost()),
            Currency.USD,
            "Success payment"
        );
        errorPaymentResponse = new PaymentResponse(
            PaymentStatus.ERROR,
            0,
            0,
            new BigDecimal(premiumPeriod.getCost()),
            Currency.USD,
            "Error payment"
        );
        premiumArgumentCaptor = ArgumentCaptor.forClass(Premium.class);
        reset(premiumRepository, userRepository, premiumMapper, paymentServiceClient);
    }

    @Test
    @DisplayName("Buy Premium Successfully")
    void testBuyPremium() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(premiumMapper.toDto(premiumArgumentCaptor.capture())).thenReturn(premiumDto);

        PremiumDto result = premiumService.buyPremium(userId, premiumPeriod);

        verify(premiumRepository).existsByUserId(userId);
        verify(paymentServiceClient).sendPaymentRequest(paymentRequest);
        verify(userRepository).findById(userId);
        verify(premiumRepository).save(premiumArgumentCaptor.capture());
        verify(premiumMapper).toDto(premiumArgumentCaptor.capture());

        assertNotNull(result);
        assertEquals(result, premiumDto);
    }

    @Test
    @DisplayName("Buy Premium Throws PremiumAlreadyPurchasedException")
    void testBuyPremiumPremiumAlreadyPurchasedException() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(PremiumAlreadyPurchasedException.class, () -> premiumService.buyPremium(userId, premiumPeriod));
    }

    @Test
    @DisplayName("Buy Premium Throws PaymentFailureException")
    void testBuyPremiumPaymentFailureException() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(errorPaymentResponse);

        assertThrows(PaymentFailureException.class, () -> premiumService.buyPremium(userId, premiumPeriod));
    }

    @Test
    @DisplayName("Buy Premium Throws UserNotFoundException")
    void testBuyPremiumUserNotFoundException() {
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> premiumService.buyPremium(userId, premiumPeriod));
    }
}