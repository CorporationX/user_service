package school.faang.user_service.outbox.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    Page<OutboxEvent> findByStatusAndSourceService(OutboxStatus status, String sourceService, Pageable pageable);

    boolean existsByAggregateIdAndEventType(Long aggregateId, OutboxEventType eventType);
}
