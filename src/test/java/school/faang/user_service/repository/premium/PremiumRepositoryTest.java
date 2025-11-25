package school.faang.user_service.repository.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class PremiumRepositoryTest {

    @Autowired
    private PremiumRepository premiumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    private User testUser1;
    private User testUser2;
    private Premium activePremium;
    private Premium expiredPremium;
    private Premium futurePremium;

    @BeforeEach
    void setUp() {
        // Clean up test data
        premiumRepository.deleteAll();
        userRepository.deleteAll();

        // Create test country
        Country testCountry = Country.builder()
                .title("Test Country")
                .build();
        testCountry = countryRepository.save(testCountry);

        // Create test users
        testUser1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password123")
                .country(testCountry)
                .active(true)
                .build();
        testUser1 = userRepository.save(testUser1);

        testUser2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password123")
                .country(testCountry)
                .active(true)
                .build();
        testUser2 = userRepository.save(testUser2);

        // Create test premiums
        LocalDateTime now = LocalDateTime.now();
        
        activePremium = Premium.builder()
                .user(testUser1)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now.minusDays(15))
                .endDate(now.plusDays(15))
                .amount(new BigDecimal("10.00"))
                .verificationCode(12345)
                .paymentNumber(123456789L)
                .currency(Currency.USD)
                .createdAt(now.minusDays(15))
                .build();

        expiredPremium = Premium.builder()
                .user(testUser2)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .startDate(now.minusDays(45))
                .endDate(now.minusDays(15))
                .amount(new BigDecimal("10.00"))
                .verificationCode(54321)
                .paymentNumber(987654321L)
                .currency(Currency.USD)
                .createdAt(now.minusDays(45))
                .build();

        futurePremium = Premium.builder()
                .user(testUser1)
                .premiumPeriod(PremiumPeriod.QUARTERLY)
                .startDate(now.plusDays(30))
                .endDate(now.plusDays(120))
                .amount(new BigDecimal("25.00"))
                .verificationCode(11111)
                .paymentNumber(111111111L)
                .currency(Currency.USD)
                .createdAt(now.plusDays(30))
                .build();

        premiumRepository.saveAll(List.of(activePremium, expiredPremium, futurePremium));
    }

    @Test
    void existsByUserId_WithExistingUser_ShouldReturnTrue() {
        
        boolean exists = premiumRepository.existsByUserId(testUser1.getId());

        
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserId_WithNonExistingUser_ShouldReturnFalse() {
        
        boolean exists = premiumRepository.existsByUserId(999L);

        
        assertThat(exists).isFalse();
    }

    @Test
    void findAllByEndDateBefore_WithExpiredDate_ShouldReturnExpiredPremiums() {
        
        LocalDateTime cutoffDate = LocalDateTime.now();

        
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(cutoffDate);

        
        assertThat(expiredPremiums).hasSize(1);
        assertThat(expiredPremiums.get(0).getId()).isEqualTo(expiredPremium.getId());
    }

    @Test
    void findAllByEndDateBefore_WithFutureDate_ShouldReturnAllPremiums() {
        
        LocalDateTime futureDate = LocalDateTime.now().plusDays(200);

        
        List<Premium> allPremiums = premiumRepository.findAllByEndDateBefore(futureDate);

        
        assertThat(allPremiums).hasSize(3);
    }

    @Test
    void findFirstByUser_Id_WithExistingUser_ShouldReturnPremium() {
        
        Optional<Premium> premium = premiumRepository.findFirstByUser_Id(testUser1.getId());

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    void findFirstByUser_Id_WithNonExistingUser_ShouldReturnEmpty() {
        
        Optional<Premium> premium = premiumRepository.findFirstByUser_Id(999L);

        
        assertThat(premium).isEmpty();
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithActivePremium_ShouldReturnPremium() {
        
        LocalDateTime now = LocalDateTime.now();

        
        Optional<Premium> premium = premiumRepository.findFirstByUser_IdAndEndDateAfter(testUser1.getId(), now);

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getId()).isEqualTo(activePremium.getId());
        assertThat(premium.get().getEndDate()).isAfter(now);
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithExpiredPremium_ShouldReturnEmpty() {
        
        LocalDateTime now = LocalDateTime.now();

        
        Optional<Premium> premium = premiumRepository.findFirstByUser_IdAndEndDateAfter(testUser2.getId(), now);

        
        assertThat(premium).isEmpty();
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithFuturePremium_ShouldReturnPremium() {
        
        LocalDateTime now = LocalDateTime.now();

        
        Optional<Premium> premium = premiumRepository.findFirstByUser_IdAndEndDateAfter(testUser1.getId(), now);

        
        assertThat(premium).isPresent();
        // Should return the active premium, not the future one
        assertThat(premium.get().getId()).isEqualTo(activePremium.getId());
    }

    @Test
    void findFirstByUserId_WithExistingUser_ShouldReturnPremium() {
        
        Optional<Premium> premium = premiumRepository.findFirstByUserId(testUser1.getId());

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    void findFirstByUserId_WithNonExistingUser_ShouldReturnEmpty() {
        
        Optional<Premium> premium = premiumRepository.findFirstByUserId(999L);

        
        assertThat(premium).isEmpty();
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithLeapYearDate_ShouldWorkCorrectly() {
        
        LocalDateTime leapYearDate = LocalDateTime.of(2024, Month.FEBRUARY, 29, 12, 0, 0);
        LocalDateTime futureDate = leapYearDate.plusDays(30);

        
        Optional<Premium> premium = premiumRepository
                .findFirstByUser_IdAndEndDateAfter(testUser1.getId(), leapYearDate);

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getEndDate()).isAfter(leapYearDate);
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithYearBoundary_ShouldWorkCorrectly() {
        
        LocalDateTime yearEnd = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59, 59);

        
        Optional<Premium> premium = premiumRepository.findFirstByUser_IdAndEndDateAfter(testUser1.getId(), yearEnd);

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getEndDate()).isAfter(yearEnd);
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithDSTTransition_ShouldWorkCorrectly() {
        
        LocalDateTime dstDate = LocalDateTime.of(2024, Month.MARCH, 10, 2, 30, 0);

        
        Optional<Premium> premium = premiumRepository.findFirstByUser_IdAndEndDateAfter(testUser1.getId(), dstDate);

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getEndDate()).isAfter(dstDate);
    }

    @Test
    void findAllByEndDateBefore_WithLeapYearDate_ShouldWorkCorrectly() {
        
        LocalDateTime leapYearDate = LocalDateTime.of(2024, Month.FEBRUARY, 29, 12, 0, 0);
        
        // Create a premium that expires before the leap year date
        Premium expiredPremium = Premium.builder()
                .user(testUser1)
                .startDate(LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0, 0))
                .endDate(LocalDateTime.of(2024, Month.FEBRUARY, 28, 10, 0, 0))
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .paymentNumber(1001L)
                .verificationCode(123456)
                .createdAt(LocalDateTime.now())
                .build();
        premiumRepository.save(expiredPremium);

        
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(leapYearDate);

        
        assertThat(expiredPremiums).hasSize(1);
        assertThat(expiredPremiums.get(0).getEndDate()).isBefore(leapYearDate);
    }

    @Test
    void findAllByEndDateBefore_WithYearBoundary_ShouldWorkCorrectly() {
        
        LocalDateTime yearEnd = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59, 59);
        
        // Create a premium that expires before the year boundary
        Premium expiredPremium = Premium.builder()
                .user(testUser1)
                .startDate(LocalDateTime.of(2023, Month.JANUARY, 1, 10, 0, 0))
                .endDate(LocalDateTime.of(2023, Month.DECEMBER, 30, 10, 0, 0))
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .paymentNumber(1002L)
                .verificationCode(123457)
                .createdAt(LocalDateTime.now())
                .build();
        premiumRepository.save(expiredPremium);

        
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(yearEnd);

        
        assertThat(expiredPremiums).hasSize(1);
        assertThat(expiredPremiums.get(0).getEndDate()).isBefore(yearEnd);
    }

    @Test
    void findAllByEndDateBefore_WithDSTTransition_ShouldWorkCorrectly() {
        
        LocalDateTime dstDate = LocalDateTime.of(2024, Month.MARCH, 10, 2, 30, 0);
        
        // Create a premium that expires before the DST transition date
        Premium expiredPremium = Premium.builder()
                .user(testUser1)
                .startDate(LocalDateTime.of(2024, Month.FEBRUARY, 1, 10, 0, 0))
                .endDate(LocalDateTime.of(2024, Month.MARCH, 9, 10, 0, 0))
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .paymentNumber(1003L)
                .verificationCode(123458)
                .createdAt(LocalDateTime.now())
                .build();
        premiumRepository.save(expiredPremium);

        
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(dstDate);

        
        assertThat(expiredPremiums).hasSize(1);
        assertThat(expiredPremiums.get(0).getEndDate()).isBefore(dstDate);
    }

    @Test
    void findFirstByUser_Id_WithMultiplePremiums_ShouldReturnOne() {
        // Given - User1 has both active and future premiums
        
        Optional<Premium> premium = premiumRepository.findFirstByUser_Id(testUser1.getId());

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithExactEndDate_ShouldReturnEmpty() {
        
        premiumRepository.deleteAll(); // Clear all premiums from setUp
        LocalDateTime exactEndDate = LocalDateTime.of(2025, Month.DECEMBER, 31, 23, 59, 59);
        
        // Create a premium that ends exactly at the search date
        Premium testPremium = Premium.builder()
                .user(testUser1)
                .startDate(LocalDateTime.of(2025, Month.DECEMBER, 1, 10, 0, 0))
                .endDate(exactEndDate)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .paymentNumber(2001L)
                .verificationCode(200001)
                .createdAt(LocalDateTime.now())
                .build();
        premiumRepository.save(testPremium);

        
        Optional<Premium> premium = premiumRepository
                .findFirstByUser_IdAndEndDateAfter(testUser1.getId(), exactEndDate);

        
        assertThat(premium).isEmpty();
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithOneSecondAfterEndDate_ShouldReturnEmpty() {
        
        premiumRepository.deleteAll(); // Clear all premiums from setUp
        LocalDateTime endDate = LocalDateTime.of(2025, Month.DECEMBER, 31, 23, 59, 59);
        LocalDateTime oneSecondAfter = endDate.plusSeconds(1);
        
        // Create a premium that ends exactly one second before the search date
        Premium testPremium = Premium.builder()
                .user(testUser1)
                .startDate(LocalDateTime.of(2025, Month.DECEMBER, 1, 10, 0, 0))
                .endDate(endDate)
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .premiumPeriod(PremiumPeriod.MONTHLY)
                .paymentNumber(2002L)
                .verificationCode(200002)
                .createdAt(LocalDateTime.now())
                .build();
        premiumRepository.save(testPremium);

        
        Optional<Premium> premium = premiumRepository
                .findFirstByUser_IdAndEndDateAfter(testUser1.getId(), oneSecondAfter);

        
        // The testPremium should not be found because its end date is exactly one second before the search date
        assertThat(premium).isEmpty();
    }

    @Test
    void findFirstByUser_IdAndEndDateAfter_WithOneSecondBeforeEndDate_ShouldReturnPremium() {
        
        LocalDateTime oneSecondBefore = activePremium.getEndDate().minusSeconds(1);

        
        Optional<Premium> premium = premiumRepository
                .findFirstByUser_IdAndEndDateAfter(testUser1.getId(), oneSecondBefore);

        
        assertThat(premium).isPresent();
        assertThat(premium.get().getId()).isEqualTo(activePremium.getId());
    }
}
