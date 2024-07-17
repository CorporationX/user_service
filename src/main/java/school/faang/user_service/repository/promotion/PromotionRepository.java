package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionTariff;

/**
 * @author Evgenii Malkov
 */
@Repository
public interface PromotionRepository extends JpaRepository<PromotionTariff, Long> {
}
