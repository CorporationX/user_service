package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

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

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO event (title, description, start_date, end_date, location, " +
            "max_attendees, user_id, type) VALUES (:title, :description, :start_date, :end_date, :location, " +
            ":max_attendees, :user_id, :type)")
    void addEvent(String title, String description, LocalDateTime start_date, LocalDateTime end_date, String location,
            int max_attendees, long user_id, EventType type);
}