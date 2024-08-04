package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionPaymentRequest;

import java.util.UUID;

/**
 * @author Evgenii Malkov
 */
@Repository
public interface PromotionPaymentRequestRepository extends JpaRepository<PromotionPaymentRequest, UUID> {
}
