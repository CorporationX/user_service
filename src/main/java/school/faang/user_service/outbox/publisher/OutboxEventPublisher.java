package school.faang.user_service.outbox.publisher;

import school.faang.user_service.outbox.entity.OutboxEventType;

public interface OutboxEventPublisher {

    OutboxEventType getType();

    void publish(String payload);
}