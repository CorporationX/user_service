package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
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
            SELECT e.*
            FROM event e
            LEFT JOIN event_promotion ep ON e.id = ep.event_id AND ep.number_of_views > 0
            ORDER BY
                ep.coefficient DESC NULLS LAST,
                ep.creation_date ASC,
                e.created_at DESC
            OFFSET :offset
            LIMIT :limit
            """)
    List<Event> findAllSortedByPromotedEventsPerPage(@Param("offset") int offset, @Param("limit") int limit);

    List<Event> findAllByStatusAndStartDateBetween(EventStatus eventStatus, LocalDateTime now, LocalDateTime toTime);
}