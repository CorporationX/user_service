package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.PromotionPlan;

public interface PromotionPlanRepositoryAdapter extends JpaRepository<PromotionPlan, Long> {
}
