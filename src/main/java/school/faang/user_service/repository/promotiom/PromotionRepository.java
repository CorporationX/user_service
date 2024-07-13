package school.faang.user_service.repository.promotiom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    boolean existsByUserId(long userId);

    boolean existsByEventId(long eventId);
}
