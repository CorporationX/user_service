package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;

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
            LEFT JOIN event_promotion ep ON e.id = ep.event_id
            ORDER BY
                CASE WHEN ep.coefficient IS NULL THEN 1 ELSE 0 END,
                ep.coefficient DESC,
                ep.creation_date ASC
            LIMIT :limit
            OFFSET :offset
            """)
    List<Event> findAllSortedByPromotedEventsPerPage(@Param("limit") int limit, @Param("offset") int offset);
}