package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.EventPromotion;

@Repository
public interface EventPromotionRepository extends CrudRepository<EventPromotion, Long> {
}
