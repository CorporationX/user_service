package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;

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

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM event e
            WHERE e.id = :eventId AND e.user_id = :userId
            """)
    int deleteById(long userId, long eventId);

    default Event getByIdOrThrow(long eventId) {
        return findById(eventId)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Event %d not found", eventId))
                );
    }
}