package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.premium.PremiumPurchaseAttempt;

import java.util.Optional;

public interface PremiumPurchaseAttemptRepository extends JpaRepository<PremiumPurchaseAttempt, Long> {
    Optional<PremiumPurchaseAttempt> findByPaymentNumber(String paymentNumber);
}
