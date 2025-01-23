package school.faang.user_service.repository.Promotion;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p WHERE p.userId = :userId AND p.currentImpressions < p.impressionsLimit")
    List<Promotion> findActivePromotionsByUserId(Long userId);
}