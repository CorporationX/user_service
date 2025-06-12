package school.faang.user_service.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            WHERE e.user_id = :userId
            """)
    List<Event> findAllByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM event e
            JOIN user_event ue ON ue.event_id = e.id
            WHERE ue.user_id = :userId
            """)
    List<Event> findParticipatedEventsByUserId(long userId);

    @Query("""
            SELECT e 
            FROM Event e
            JOIN EventPromotion ep ON ep.event = e
            JOIN FETCH e.owner
            WHERE ep.active = true 
            AND ep.plan = :plan 
            """)
    Slice<Event> findAllActivePromotionByPlan(@Param("plan") Plan plan, Pageable pageable);

    @Query("""
            SELECT e 
            FROM Event e
            JOIN FETCH e.owner
             WHERE NOT EXISTS (
               SELECT ep 
                 FROM EventPromotion ep
                WHERE ep.event = e
                  AND ep.active  = true
             )
            """)
    Slice<Event> findAllWithoutPromotion(Pageable pageable);
}