package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.dto.PaymentRequest;
import school.faang.user_service.client.dto.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.enums.PurchaseStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.premium.PremiumCacheService;
import school.faang.user_service.service.premium.PremiumService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled // TODO: Удалить после исправление
@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumPurchaseAttemptRepository attemptRepository;

    @Mock
    private PaymentServiceClient paymentClient;

    @Mock
    private PremiumMapper premiumMapper;

    @Mock
    private PremiumCacheService premiumCacheService;

    @Mock
    private PremiumBoughtEventPublisher eventPublisher;

    @InjectMocks
    private PremiumService premiumService;

    private User testUser;
    private Premium testPremium;
    private PremiumPurchaseAttempt testAttempt;
    private PaymentResponse successResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testPremium = Premium.builder()
                .id(1L)
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();

        testAttempt = PremiumPurchaseAttempt.builder()
                .id(1L)
                .userId("1")
                .paymentNumber("PREM-1-test123")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        successResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                123456789L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment successful"
        );
    }

    @Test
    void buyPremium_WhenUserNotFound_ShouldThrowNotFoundException() {

        long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> premiumService.buyPremium(userId, PremiumPeriod.MONTHLY))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found: 999");
    }

    @Test
    void buyPremium_WhenNewPurchase_ShouldCreateNewPremium() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(testAttempt);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        PremiumDto result = premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPremiumPeriod()).isEqualTo(PremiumPeriod.MONTHLY);

        verify(paymentClient).processPayment(any(PaymentRequest.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumCacheService).setActiveUntil(eq(1L), any(LocalDateTime.class));
        verify(attemptRepository, times(3)).save(any(PremiumPurchaseAttempt.class));
        verify(eventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_WhenExistingActivePremium_ShouldExtendPremium() {

        LocalDateTime existingEndDate = LocalDateTime.now().plusDays(15);
        Premium existingPremium = Premium.builder()
                .id(1L)
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now().minusDays(15))
                .endDate(existingEndDate)
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now().minusDays(15))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(testAttempt);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingPremium));
        when(premiumRepository.save(any(Premium.class))).thenReturn(existingPremium);
        when(premiumMapper.toDto(existingPremium)).thenReturn(createPremiumDto());

        PremiumDto result = premiumService.buyPremium(1L, PremiumPeriod.QUARTERLY);

        assertThat(result).isNotNull();
        verify(premiumRepository).save(existingPremium);
        verify(premiumCacheService).setActiveUntil(eq(1L), any(LocalDateTime.class));
        verify(eventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_WhenPaymentFails_ShouldThrowPaymentFailedException() {

        PaymentResponse failedResponse = new PaymentResponse(
                null, 0, 0L, BigDecimal.ZERO, Currency.USD, "Payment failed"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(failedResponse);

        assertThatThrownBy(() -> premiumService.buyPremium(1L, PremiumPeriod.MONTHLY))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Payment failed");

        verify(attemptRepository, times(2)).save(any(PremiumPurchaseAttempt.class));
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    void buyPremium_WhenPaymentThrowsException_ShouldThrowPaymentFailedException() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentClient.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Network error"));

        assertThatThrownBy(() -> premiumService.buyPremium(1L, PremiumPeriod.MONTHLY))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Payment processing error");

        verify(attemptRepository, times(2)).save(any(PremiumPurchaseAttempt.class));
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    void buyPremium_WhenAttemptAlreadyCompleted_ShouldReturnExistingPremium() {

        PremiumPurchaseAttempt completedAttempt = PremiumPurchaseAttempt.builder()
                .id(1L)
                .userId("1")
                .paymentNumber("PREM-1-test123")
                .status(PurchaseStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.of(completedAttempt));
        when(premiumRepository.findFirstByUser_Id(1L)).thenReturn(Optional.of(testPremium));
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        PremiumDto result = premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        assertThat(result).isNotNull();
        verify(paymentClient, never()).processPayment(any(PaymentRequest.class));
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    void buyPremium_WhenAttemptPaymentSuccess_ShouldSkipPaymentProcessing() {

        PremiumPurchaseAttempt successAttempt = PremiumPurchaseAttempt.builder()
                .id(1L)
                .userId("1")
                .paymentNumber("PREM-1-test123")
                .status(PurchaseStatus.PAYMENT_SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.of(successAttempt));
        when(premiumRepository.findFirstByUser_Id(1L)).thenReturn(Optional.of(testPremium));
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());

        PremiumDto result = premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        assertThat(result).isNotNull();
        verify(paymentClient, never()).processPayment(any(PaymentRequest.class));
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    void cancelPremium_WhenPremiumNotFound_ShouldThrowNotFoundException() {

        when(premiumRepository.findFirstByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> premiumService.cancelPremium(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Premium subscription not found for user: 1");
    }

    @Test
    void cancelPremium_WhenPremiumAlreadyExpired_ShouldEvictCacheAndReturn() {

        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1);
        Premium expiredPremium = Premium.builder()
                .id(1L)
                .user(testUser)
                .endDate(expiredDate)
                .build();

        when(premiumRepository.findFirstByUserId(1L)).thenReturn(Optional.of(expiredPremium));

        premiumService.cancelPremium(1L);

        verify(premiumCacheService).evict(1L);
        verify(premiumRepository, never()).save(any(Premium.class));
    }

    @Test
    void cancelPremium_WhenPremiumActive_ShouldCancelAndEvictCache() {

        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
        Premium activePremium = Premium.builder()
                .id(1L)
                .user(testUser)
                .endDate(futureDate)
                .build();

        when(premiumRepository.findFirstByUserId(1L)).thenReturn(Optional.of(activePremium));
        when(premiumRepository.save(any(Premium.class))).thenReturn(activePremium);

        premiumService.cancelPremium(1L);

        verify(premiumRepository).save(activePremium);
        verify(premiumCacheService).evict(1L);
        assertThat(activePremium.getEndDate()).isBefore(futureDate);
    }

    @ParameterizedTest
    @EnumSource(PremiumPeriod.class)
    void buyPremium_WithDifferentPeriods_ShouldCalculateCorrectEndDate(PremiumPeriod period) {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(testAttempt);
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, period);


        verify(premiumRepository).save(any(Premium.class));
        verify(premiumCacheService).setActiveUntil(eq(1L), any(LocalDateTime.class));
        verify(eventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_WhenRaceConditionInAttemptCreation_ShouldHandleGracefully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString()))
                .thenReturn(Optional.empty()) // First call in getOrCreateAttempt
                .thenReturn(Optional.of(testAttempt)); // Second call after exception
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate key"))
                .thenAnswer(invocation -> invocation.getArgument(0)); // For subsequent saves
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenReturn(testPremium);
        when(premiumMapper.toDto(testPremium)).thenReturn(createPremiumDto());


        PremiumDto result = premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();
        verify(attemptRepository, times(2)).findByPaymentNumber(anyString());
        verify(eventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    private PremiumDto createPremiumDto() {
        return PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .amount(new BigDecimal("10.00"))
                .paymentNumber("PREM-1-test123")
                .verificationCode(12345)
                .currency(Currency.USD)
                .createdAt(LocalDateTime.now())
                .build();
    }
}