package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Event;

import java.awt.print.Pageable;
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

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM events AS e
            JOIN events_promotion AS ep ON ep.event_id = e.id
            WHERE ep.active = true AND ep.plan = 'VIP';
            """)
    List<Event> findAllPromotedVip(Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM events AS e
            JOIN events_promotion AS ep ON ep.event_id = e.id
            WHERE ep.active = true AND ep.plan = 'GOLD';
            """)
    List<Event> findAllPromotedGold();

    @Query(nativeQuery = true, value = """
            SELECT e.* FROM events AS e
            JOIN events_promotion AS ep ON ep.event_id = e.id
            WHERE ep.active = true AND ep.plan = 'PLUS';
            """)
    List<Event> findAllPromotedPlus();
}