package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.Map;

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
            WITH to_delete AS (
                SELECT * FROM event
                WHERE end_date < NOW()
                FOR UPDATE
            ),
            deleted AS (
                DELETE FROM event
                WHERE id IN (SELECT id FROM to_delete)
                RETURNING *
            )
            SELECT count(*) FROM deleted
            """)
    int deleteAllEndedInPast();

    @Query(nativeQuery = true, value = """
            SELECT user_id, COUNT(*)
            FROM event
            GROUP BY user_id
            HAVING COUNT(*) > 0
            """)
    Map<Long, Integer> countOwnedEventsPerUser();
}