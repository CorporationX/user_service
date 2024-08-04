package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.LinkEventPromotion;

/**
 * @author Evgenii Malkov
 */
@Repository
public interface LinkEventPromotionRepository extends JpaRepository<LinkEventPromotion, Long> {
}
