package school.faang.user_service.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusAndSourceService(OutboxStatus status, String sourceService);

    boolean existsByAggregateIdAndEventType(Long aggregateId, OutboxEventType eventType);
}
