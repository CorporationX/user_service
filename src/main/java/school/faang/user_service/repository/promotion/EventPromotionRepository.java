package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.event.EventPromotion;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Long> {
    boolean existsByEventIdAndActiveTrue(long id);
}
