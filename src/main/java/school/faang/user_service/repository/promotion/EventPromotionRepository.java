package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.event.EventPromotion;

import java.time.LocalDateTime;

public interface EventPromotionRepository extends JpaRepository<EventPromotion, Long> {
    @Query("""
            SELECT e
            FROM EventPromotion e
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.percentage = :percentage
                AND e.feedRank = :feedRank
            """)
    EventPromotion findSamePromotion(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                     Integer percentage, Integer feedRank);

    @Query("""
            SELECT e
            FROM EventPromotion e
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
            """)
    EventPromotion findPromotionByEventIdStartDateEndDate(Long eventId, LocalDateTime startDate,
                                                          LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("""
            UPDATE EventPromotion e
            SET e.percentage = :newPercentage
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.feedRank = :feedRank
            """)
    void updatePromotionPercentage(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                   Integer newPercentage, Integer feedRank);

    @Query("""
            SELECT e.percentage
            FROM EventPromotion e
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.feedRank = :feedRank
            """)
    Integer getUserPercentage(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer feedRank);

    @Query("""
            SELECT e.feedRank
            FROM EventPromotion e
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.percentage = :percentage
            """)
    Integer getEventFeedRank(Long eventId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

    @Modifying
    @Transactional
    @Query("""
            UPDATE EventPromotion e
            SET e.feedRank = :newFeedRank
            WHERE e.event.id = :eventId
                AND e.startDate = :startDate
                AND e.endDate = :endDate
                AND e.percentage = :percentage
            """)
    void updateEventFeedRank(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                             Integer percentage, Integer newFeedRank);
}
