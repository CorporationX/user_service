package school.faang.user_service.service.event;

import lombok.NonNull;

public interface EventPublisherService {
    void publishEvent(@NonNull Object event, @NonNull String eventId, @NonNull String topic);
}
