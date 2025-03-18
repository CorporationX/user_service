package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.event.EventPromotionCount;

public interface EventPromotionCountRepository extends JpaRepository<EventPromotionCount, Long> {
    @Query("SELECT e.count FROM EventPromotionCount e WHERE e.id = :id")
    Integer findCountByEventId(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE EventPromotionCount e SET e.count = e.count + :increment WHERE e.eventId = :eventId")
    void incrementCountByEventId(Long eventId, Integer increment);
}
