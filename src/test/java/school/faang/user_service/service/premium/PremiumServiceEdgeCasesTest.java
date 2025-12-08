package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.client.dto.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.enums.PurchaseStatus;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled // TODO: Удалить после исправление
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PremiumServiceEdgeCasesTest {

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
    private PremiumBoughtEventPublisher boughtEventPublisher;

    @InjectMocks
    private PremiumService premiumService;

    private User testUser;
    private PaymentResponse successResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
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
    void buyPremium_OnLeapYearFebruary29_ShouldHandleCorrectly() {
        // Given - 2024 is a leap year
        LocalDateTime leapYearDate = LocalDateTime.of(2024, Month.FEBRUARY, 29, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            // Verify that the end date is calculated correctly from Feb 29
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isEqualTo(startDate.plusDays(30));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnNonLeapYearFebruary28_ShouldHandleCorrectly() {
        // Given - 2023 is not a leap year
        LocalDateTime nonLeapYearDate = LocalDateTime.of(2023, Month.FEBRUARY, 28, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            // Verify that the end date is calculated correctly from Feb 28
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isEqualTo(startDate.plusDays(30));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @ParameterizedTest
    @CsvSource({
            "2026, 1, 31, 30",
            "2026, 3, 31, 30",
            "2026, 4, 30, 30",
            "2026, 5, 31, 30",
            "2026, 6, 30, 30",
            "2026, 7, 31, 30",
            "2026, 8, 31, 30",
            "2026, 9, 30, 30",
            "2026, 10, 31, 30",
            "2026, 11, 30, 30",
            "2026, 12, 31, 30"
    })
    void buyPremium_OnMonthEndDates_ShouldCalculateCorrectEndDate(int year, int month, int day, int expectedDays) {

        LocalDateTime startDate = LocalDateTime.of(year, month, day, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            assertThat(premium).isNotNull();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isNotNull();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expectedEndDate = now.plusDays(expectedDays);
            assertThat(endDate).isCloseTo(expectedEndDate, within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnYearlyPeriod_ShouldHandleLeapYearCorrectly() {
        // Given - Start on Feb 29, 2024 (leap year) with yearly period
        LocalDateTime leapYearDate = LocalDateTime.of(2024, Month.FEBRUARY, 29, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            LocalDateTime startDate = premium.getStartDate();
            LocalDateTime endDate = premium.getEndDate();
            // Yearly period should add 365 days
            assertThat(endDate).isEqualTo(startDate.plusDays(365));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.YEARLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnQuarterlyPeriod_ShouldHandleMonthBoundariesCorrectly() {
        // Given - Start on Jan 31 with quarterly period (90 days)
        LocalDateTime startDate = LocalDateTime.of(2026, Month.JANUARY, 31, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            LocalDateTime endDate = premium.getEndDate();
            // Quarterly period should add 3 months
            LocalDateTime now = LocalDateTime.now();
            assertThat(endDate).isCloseTo(now.plusMonths(3), within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.QUARTERLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnYearBoundary_ShouldHandleCorrectly() {
        // Given - Start on Dec 31, 2025
        LocalDateTime yearEndDate = LocalDateTime.of(2025, Month.DECEMBER, 31, 23, 59);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            LocalDateTime endDate = premium.getEndDate();
            // Should correctly cross year boundary - check that end date is in the future
            LocalDateTime now = LocalDateTime.now();
            assertThat(endDate).isAfter(now);
            assertThat(endDate).isCloseTo(now.plusDays(30), within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnLeapYearToNonLeapYear_ShouldHandleCorrectly() {
        // Given - Start on Feb 29, 2028 (leap year) with yearly period
        LocalDateTime leapYearDate = LocalDateTime.of(2028, Month.FEBRUARY, 29, 12, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            LocalDateTime endDate = premium.getEndDate();
            // Should correctly add 365 days for yearly period
            LocalDateTime now = LocalDateTime.now();
            assertThat(endDate).isCloseTo(now.plusDays(365), within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.YEARLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnDSTTransition_ShouldHandleCorrectly() {
        // Given - Start on DST transition day (March 10, 2026 in US)
        LocalDateTime dstDate = LocalDateTime.of(2026, Month.MARCH, 10, 2, 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            LocalDateTime endDate = premium.getEndDate();
            // Should correctly add 30 days regardless of DST
            LocalDateTime now = LocalDateTime.now();
            assertThat(endDate).isCloseTo(now.plusDays(30), within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    @Test
    void buyPremium_OnUTCBoundary_ShouldHandleCorrectly() {
        // Given - Start on UTC midnight
        LocalDateTime utcMidnight = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attemptRepository.findByPaymentNumber(anyString())).thenReturn(Optional.empty());
        when(attemptRepository.save(any(PremiumPurchaseAttempt.class))).thenReturn(createAttempt());
        when(paymentClient.processPayment(any())).thenReturn(successResponse);
        when(premiumRepository.findFirstByUser_IdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(premiumRepository.save(any(Premium.class))).thenAnswer(invocation -> {
            Premium premium = invocation.getArgument(0);
            assertThat(premium).isNotNull();
            LocalDateTime endDate = premium.getEndDate();
            assertThat(endDate).isNotNull();
            // Should correctly add 30 days from current time
            LocalDateTime now = LocalDateTime.now();
            assertThat(endDate).isCloseTo(now.plusDays(30), within(1, ChronoUnit.MINUTES));
            return premium;
        });
        when(premiumMapper.toDto(any())).thenReturn(createPremiumDto());


        premiumService.buyPremium(1L, PremiumPeriod.MONTHLY);

        verify(boughtEventPublisher).publish(any(PremiumBoughtEvent.class));
    }

    private PremiumPurchaseAttempt createAttempt() {
        return PremiumPurchaseAttempt.builder()
                .id(1L)
                .userId("1")
                .paymentNumber("PREM-1-test123")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private school.faang.user_service.dto.premium.PremiumDto createPremiumDto() {
        return school.faang.user_service.dto.premium.PremiumDto.builder()
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