package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.User;
import school.faang.user_service.model.event.Event;

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

    boolean existsByTitleAndStartDateAndEndDateAndOwnerAndLocationAndMaxAttendees(
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate,
            User owner,
            String location,
            int maxAttendees
    );

    Iterable<Long> owner(User owner);

    List<Event> findByEndDateBefore(LocalDateTime dateTime);
}