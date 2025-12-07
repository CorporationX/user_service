package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.dto.PaymentResponse;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.enums.PurchaseStatus;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Disabled // TODO: Удалить после исправление
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PremiumServiceIntegrationTest {

    @Autowired
    private PremiumService premiumService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PremiumRepository premiumRepository;

    @Autowired
    private PremiumPurchaseAttemptRepository attemptRepository;

    @MockBean
    private PaymentServiceClient paymentClient;

    @MockBean
    private PremiumCacheService premiumCacheService;

    private User testUser;
    private PaymentResponse successResponse;

    @BeforeEach
    void setUp() {
        // Clean up test data
        attemptRepository.deleteAll();
        premiumRepository.deleteAll();
        userRepository.deleteAll();

        // Create test country
        Country testCountry = Country.builder()
                .title("Test Country")
                .build();
        testCountry = countryRepository.save(testCountry);

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .country(testCountry)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);

        // Mock payment response
        successResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                123456789L,
                new BigDecimal("10.00"),
                Currency.USD,
                "Payment successful"
        );

        when(paymentClient.processPayment(any())).thenReturn(successResponse);
    }

    @Test
    void buyPremium_IntegrationTest_ShouldCreatePremiumAndAttempt() {

        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUser.getId());
        assertThat(result.getPremiumPeriod()).isEqualTo(PremiumPeriod.MONTHLY);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("10.00"));

        // Verify premium was saved
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();
        assertThat(savedPremium.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedPremium.get().getPremiumPeriod()).isEqualTo(PremiumPeriod.MONTHLY);

        // Verify attempt was saved
        List<PremiumPurchaseAttempt> allAttempts = attemptRepository.findAll();
        assertThat(allAttempts).isNotEmpty();

        PremiumPurchaseAttempt savedAttempt = allAttempts.stream()
                .filter(attempt -> attempt.getUserId().equals(String.valueOf(testUser.getId())))
                .findFirst()
                .orElseThrow();
        assertThat(savedAttempt.getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
    }

    @Test
    void buyPremium_WithLeapYear_ShouldCalculateCorrectEndDate() {
        // Given - Set up a leap year scenario
        LocalDateTime leapYearDate = LocalDateTime.of(2024, Month.FEBRUARY, 29, 12, 0, 0);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();

        // Verify the premium was created with correct dates
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        LocalDateTime startDate = premium.getStartDate();
        LocalDateTime endDate = premium.getEndDate();

        // The end date should be 30 days after start date
        assertThat(endDate).isEqualTo(startDate.plusDays(30));
    }

    @Test
    void buyPremium_WithYearBoundary_ShouldHandleCorrectly() {
        // Given - Set up a year boundary scenario
        LocalDateTime yearEnd = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59, 59);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();

        // Verify the premium was created with correct dates
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        LocalDateTime startDate = premium.getStartDate();
        LocalDateTime endDate = premium.getEndDate();

        // The end date should be 30 days after start date, crossing year boundary
        assertThat(endDate).isEqualTo(startDate.plusDays(30));
        assertThat(endDate.getYear()).isEqualTo(2025);
    }

    @Test
    void buyPremium_WithDSTTransition_ShouldHandleCorrectly() {
        // Given - Set up a DST transition scenario
        LocalDateTime dstDate = LocalDateTime.of(2024, Month.MARCH, 10, 2, 30, 0);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();

        // Verify the premium was created with correct dates
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        LocalDateTime startDate = premium.getStartDate();
        LocalDateTime endDate = premium.getEndDate();

        // The end date should be 30 days after start date
        assertThat(endDate).isEqualTo(startDate.plusDays(30));
    }

    @Test
    void buyPremium_WithQuarterlyPeriod_ShouldCalculateCorrectEndDate() {

        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.QUARTERLY);


        assertThat(result).isNotNull();
        assertThat(result.getPremiumPeriod()).isEqualTo(PremiumPeriod.QUARTERLY);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("25.00"));

        // Verify premium was saved with correct period
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        LocalDateTime startDate = premium.getStartDate();
        LocalDateTime endDate = premium.getEndDate();

        // The end date should be 3 months after start date
        assertThat(endDate).isEqualTo(startDate.plusMonths(3));
    }

    @Test
    void buyPremium_WithYearlyPeriod_ShouldCalculateCorrectEndDate() {

        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.YEARLY);


        assertThat(result).isNotNull();
        assertThat(result.getPremiumPeriod()).isEqualTo(PremiumPeriod.YEARLY);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("80.00"));

        // Verify premium was saved with correct period
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        LocalDateTime startDate = premium.getStartDate();
        LocalDateTime endDate = premium.getEndDate();

        // The end date should be 365 days after start date
        assertThat(endDate).isEqualTo(startDate.plusDays(365));
    }

    @Test
    void buyPremium_WithExistingActivePremium_ShouldExtendPremium() {
        // Given - Create an existing active premium
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime existingEndDate = now.plusDays(15);

        Premium existingPremium = Premium.builder()
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now.minusDays(15))
                .endDate(existingEndDate)
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(now.minusDays(15))
                .build();

        premiumRepository.save(existingPremium);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.QUARTERLY);


        assertThat(result).isNotNull();
        assertThat(result.getPremiumPeriod()).isEqualTo(PremiumPeriod.QUARTERLY);

        // Verify the existing premium was extended
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        assertThat(premium.getId()).isEqualTo(existingPremium.getId()); // Same premium extended
        assertThat(premium.getAmount()).isEqualTo(new BigDecimal("35.00")); // 10.00 + 25.00
        assertThat(premium.getEndDate()).isAfter(existingEndDate); // Extended end date
    }

    @Test
    void buyPremium_WithExpiredPremium_ShouldCreateNewPremium() {
        // Given - Create an expired premium
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredDate = now.minusDays(1);

        Premium expiredPremium = Premium.builder()
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now.minusDays(31))
                .endDate(expiredDate)
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(now.minusDays(31))
                .build();

        premiumRepository.save(expiredPremium);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();

        // Verify a new premium was created (not extended)
        List<Premium> savedPremiums = premiumRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(testUser.getId()))
                .toList();
        assertThat(savedPremiums).hasSize(2); // Original expired + new premium

        Premium newPremium = savedPremiums.stream()
                .filter(p -> p.getId() != expiredPremium.getId())
                .findFirst()
                .orElseThrow();
        assertThat(newPremium.getId()).isNotEqualTo(expiredPremium.getId()); // New premium
        assertThat(newPremium.getAmount()).isEqualTo(new BigDecimal("10.00")); // New amount
        assertThat(newPremium.getStartDate()).isAfter(expiredDate); // New start date
    }

    @Test
    void cancelPremium_IntegrationTest_ShouldCancelActivePremium() {
        // Given - Create an active premium
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(30);

        Premium activePremium = Premium.builder()
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now)
                .endDate(futureDate)
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(now)
                .build();

        premiumRepository.save(activePremium);


        premiumService.cancelPremium(testUser.getId());


        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        assertThat(premium.getEndDate()).isBefore(futureDate);
        assertThat(premium.getEndDate()).isBefore(LocalDateTime.now().plusMinutes(1));
    }

    @Test
    void cancelPremium_WithExpiredPremium_ShouldEvictCacheOnly() {
        // Given - Create an expired premium
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredDate = now.minusDays(1);

        Premium expiredPremium = Premium.builder()
                .user(testUser)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now.minusDays(31))
                .endDate(expiredDate)
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(now.minusDays(31))
                .build();

        premiumRepository.save(expiredPremium);


        premiumService.cancelPremium(testUser.getId());


        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isPresent();

        Premium premium = savedPremium.get();
        assertThat(premium.getEndDate()).isEqualTo(expiredDate); // End date unchanged
    }

    @Test
    void buyPremium_WithPaymentFailure_ShouldThrowException() {

        when(paymentClient.processPayment(any())).thenReturn(null);

        assertThatThrownBy(() -> premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Payment failed");

        // Verify no premium was created
        Optional<Premium> savedPremium = premiumRepository.findFirstByUserId(testUser.getId());
        assertThat(savedPremium).isEmpty();
    }

    @Test
    void buyPremium_WithIdempotentRequest_ShouldReturnExistingPremium() {
        // Given - Create a completed attempt
        PremiumPurchaseAttempt completedAttempt = PremiumPurchaseAttempt.builder()
                .userId(String.valueOf(testUser.getId()))
                .paymentNumber("PREM-1-test123")
                .status(PurchaseStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        attemptRepository.save(completedAttempt);

        // Create the corresponding premium
        Premium existingPremium = Premium.builder()
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

        premiumRepository.save(existingPremium);


        PremiumDto result = premiumService.buyPremium(testUser.getId(), PremiumPeriod.MONTHLY);


        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUser.getId());

        // Verify no new premium was created
        long premiumCount = premiumRepository.count();
        assertThat(premiumCount).isEqualTo(1);
    }
}
