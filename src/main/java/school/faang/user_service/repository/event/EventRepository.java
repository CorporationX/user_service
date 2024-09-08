package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Query(value = """
            SELECT e FROM Event e 
            LEFT JOIN FETCH e.relatedSkills WHERE e.id = :eventId
            """)
    Optional<Event> findByIdWithRelatedSkills(Long eventId);

    @Query(nativeQuery = true, value = """
    SELECT e.* FROM event e 
    WHERE DATE_TRUNC('minute', e.start_date) = DATE_TRUNC('minute', CAST(? AS timestamp))
    """)
    List<Event> findEventByStartDate(LocalDateTime dateTime);



}