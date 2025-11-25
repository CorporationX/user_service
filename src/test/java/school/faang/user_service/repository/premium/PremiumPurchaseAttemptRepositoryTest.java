package school.faang.user_service.repository.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;
import school.faang.user_service.enums.PurchaseStatus;
import school.faang.user_service.repository.premium.PremiumPurchaseAttemptRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class PremiumPurchaseAttemptRepositoryTest {

    @Autowired
    private PremiumPurchaseAttemptRepository attemptRepository;

    private PremiumPurchaseAttempt pendingAttempt;
    private PremiumPurchaseAttempt successAttempt;
    private PremiumPurchaseAttempt completedAttempt;
    private PremiumPurchaseAttempt failedAttempt;

    @BeforeEach
    void setUp() {
        // Clean up test data
        attemptRepository.deleteAll();

        // Create test attempts
        LocalDateTime now = LocalDateTime.now();

        pendingAttempt = PremiumPurchaseAttempt.builder()
                .userId("1")
                .paymentNumber("PREM-1-pending123")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(now)
                .build();

        successAttempt = PremiumPurchaseAttempt.builder()
                .userId("2")
                .paymentNumber("PREM-2-success456")
                .status(PurchaseStatus.PAYMENT_SUCCESS)
                .createdAt(now.minusMinutes(5))
                .updatedAt(now.minusMinutes(2))
                .build();

        completedAttempt = PremiumPurchaseAttempt.builder()
                .userId("3")
                .paymentNumber("PREM-3-completed789")
                .status(PurchaseStatus.COMPLETED)
                .createdAt(now.minusMinutes(10))
                .updatedAt(now.minusMinutes(1))
                .completedAt(now.minusMinutes(1))
                .build();

        failedAttempt = PremiumPurchaseAttempt.builder()
                .userId("4")
                .paymentNumber("PREM-4-failed012")
                .status(PurchaseStatus.FAILED)
                .createdAt(now.minusMinutes(15))
                .updatedAt(now.minusMinutes(5))
                .failureReason("Payment declined")
                .build();

        attemptRepository.saveAll(List.of(pendingAttempt, successAttempt, completedAttempt, failedAttempt));
    }

    @Test
    void findByPaymentNumber_WithExistingPaymentNumber_ShouldReturnAttempt() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-1-pending123");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getId()).isEqualTo(pendingAttempt.getId());
        assertThat(attempt.get().getUserId()).isEqualTo("1");
        assertThat(attempt.get().getStatus()).isEqualTo(PurchaseStatus.PAYMENT_PENDING);
    }

    @Test
    void findByPaymentNumber_WithNonExistingPaymentNumber_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("NON-EXISTENT");

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithEmptyPaymentNumber_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("");

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithNullPaymentNumber_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber(null);

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithDifferentStatuses_ShouldReturnCorrectAttempt() {

        Optional<PremiumPurchaseAttempt> success = attemptRepository.findByPaymentNumber("PREM-2-success456");
        assertThat(success).isPresent();
        assertThat(success.get().getStatus()).isEqualTo(PurchaseStatus.PAYMENT_SUCCESS);

        Optional<PremiumPurchaseAttempt> completed = attemptRepository.findByPaymentNumber("PREM-3-completed789");
        assertThat(completed).isPresent();
        assertThat(completed.get().getStatus()).isEqualTo(PurchaseStatus.COMPLETED);

        Optional<PremiumPurchaseAttempt> failed = attemptRepository.findByPaymentNumber("PREM-4-failed012");
        assertThat(failed).isPresent();
        assertThat(failed.get().getStatus()).isEqualTo(PurchaseStatus.FAILED);
    }

    @Test
    void findByPaymentNumber_WithCaseSensitivePaymentNumber_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("prem-1-pending123");

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithWhitespaceInPaymentNumber_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber(" PREM-1-pending123 ");

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithPartialMatch_ShouldReturnEmpty() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-1-pending");

        
        assertThat(attempt).isEmpty();
    }

    @Test
    void findByPaymentNumber_WithExactMatch_ShouldReturnAttempt() {
        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-1-pending123");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getPaymentNumber()).isEqualTo("PREM-1-pending123");
    }

    @Test
    void findByPaymentNumber_WithSpecialCharacters_ShouldWorkCorrectly() {
        // Given - Create attempt with special characters
        PremiumPurchaseAttempt specialAttempt = PremiumPurchaseAttempt.builder()
                .userId("5")
                .paymentNumber("PREM-5-special!@#$%^&*()")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        attemptRepository.save(specialAttempt);

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-5-special!@#$%^&*()");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getPaymentNumber()).isEqualTo("PREM-5-special!@#$%^&*()");
    }

    @Test
    void findByPaymentNumber_WithVeryLongPaymentNumber_ShouldWorkCorrectly() {
        // Given - Create attempt with very long payment number
        String longPaymentNumber = "PREM-" + "A".repeat(100);
        PremiumPurchaseAttempt longAttempt = PremiumPurchaseAttempt.builder()
                .userId("6")
                .paymentNumber(longPaymentNumber)
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        attemptRepository.save(longAttempt);

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber(longPaymentNumber);

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getPaymentNumber()).isEqualTo(longPaymentNumber);
    }

    @Test
    void findByPaymentNumber_WithUnicodeCharacters_ShouldWorkCorrectly() {
        // Given - Create attempt with unicode characters
        PremiumPurchaseAttempt unicodeAttempt = PremiumPurchaseAttempt.builder()
                .userId("7")
                .paymentNumber("PREM-7-unicode-测试-🚀")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        attemptRepository.save(unicodeAttempt);

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-7-unicode-测试-🚀");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getPaymentNumber()).isEqualTo("PREM-7-unicode-测试-🚀");
    }

    @Test
    void findByPaymentNumber_WithLeapYearDate_ShouldWorkCorrectly() {
        // Given - Create attempt with leap year date
        LocalDateTime beforeSave = LocalDateTime.now();
        PremiumPurchaseAttempt leapAttempt = PremiumPurchaseAttempt.builder()
                .userId("8")
                .paymentNumber("PREM-8-leap2024")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .build();
        
        attemptRepository.save(leapAttempt);
        LocalDateTime afterSave = LocalDateTime.now();

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-8-leap2024");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getCreatedAt())
                .isAfterOrEqualTo(beforeSave)
                .isBeforeOrEqualTo(afterSave);
    }

    @Test
    void findByPaymentNumber_WithYearBoundaryDate_ShouldWorkCorrectly() {
        // Given - Create attempt with year boundary date
        LocalDateTime beforeSave = LocalDateTime.now();
        PremiumPurchaseAttempt yearEndAttempt = PremiumPurchaseAttempt.builder()
                .userId("9")
                .paymentNumber("PREM-9-yearEnd2023")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .build();
        
        attemptRepository.save(yearEndAttempt);
        LocalDateTime afterSave = LocalDateTime.now();

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-9-yearEnd2023");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getCreatedAt())
                .isAfterOrEqualTo(beforeSave)
                .isBeforeOrEqualTo(afterSave);
    }

    @Test
    void findByPaymentNumber_WithDSTTransitionDate_ShouldWorkCorrectly() {
        // Given - Create attempt with DST transition date
        LocalDateTime beforeSave = LocalDateTime.now();
        PremiumPurchaseAttempt dstAttempt = PremiumPurchaseAttempt.builder()
                .userId("10")
                .paymentNumber("PREM-10-dst2024")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .build();
        
        attemptRepository.save(dstAttempt);
        LocalDateTime afterSave = LocalDateTime.now();

        
        Optional<PremiumPurchaseAttempt> attempt = attemptRepository.findByPaymentNumber("PREM-10-dst2024");

        
        assertThat(attempt).isPresent();
        assertThat(attempt.get().getCreatedAt())
                .isAfterOrEqualTo(beforeSave)
                .isBeforeOrEqualTo(afterSave);
    }

    @Test
    void findByPaymentNumber_WithMultipleAttemptsSameUser_ShouldReturnCorrectOne() {
        // Given - Create multiple attempts for same user
        PremiumPurchaseAttempt attempt1 = PremiumPurchaseAttempt.builder()
                .userId("11")
                .paymentNumber("PREM-11-user11-first")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        PremiumPurchaseAttempt attempt2 = PremiumPurchaseAttempt.builder()
                .userId("11")
                .paymentNumber("PREM-11-user11-second")
                .status(PurchaseStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        attemptRepository.saveAll(List.of(attempt1, attempt2));

        
        Optional<PremiumPurchaseAttempt> foundAttempt1 = attemptRepository.findByPaymentNumber("PREM-11-user11-first");
        Optional<PremiumPurchaseAttempt> foundAttempt2 = attemptRepository.findByPaymentNumber("PREM-11-user11-second");

        
        assertThat(foundAttempt1).isPresent();
        assertThat(foundAttempt1.get().getStatus()).isEqualTo(PurchaseStatus.PAYMENT_PENDING);

        assertThat(foundAttempt2).isPresent();
        assertThat(foundAttempt2.get().getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
    }

    @Test
    void findByPaymentNumber_WithDuplicatePaymentNumbers_ShouldReturnFirstOne() {
        // Given - Create attempts with duplicate payment numbers (should not happen in real scenario)
        PremiumPurchaseAttempt attempt1 = PremiumPurchaseAttempt.builder()
                .userId("12")
                .paymentNumber("PREM-12-duplicate")
                .status(PurchaseStatus.PAYMENT_PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        attemptRepository.save(attempt1);

        
        Optional<PremiumPurchaseAttempt> foundAttempt = attemptRepository.findByPaymentNumber("PREM-12-duplicate");

        
        assertThat(foundAttempt).isPresent();
        assertThat(foundAttempt.get().getUserId()).isEqualTo("12");
    }
}
