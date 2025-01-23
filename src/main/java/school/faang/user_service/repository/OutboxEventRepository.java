package school.faang.user_service.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findAllByProcessedFalse();

    @Modifying
    @Transactional
    @Query("DELETE FROM OutboxEvent e WHERE e.processed = true")
    int deleteProcessedEvents();
}
