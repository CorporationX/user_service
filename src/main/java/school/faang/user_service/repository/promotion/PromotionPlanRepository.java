package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.Optional;

public interface PromotionPlanRepository extends JpaRepository<PromotionPlan, Integer> {
    Optional<PromotionPlan> findByPlan(Plan type);
}
