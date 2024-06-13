package school.faang.user_service.publisher;

import school.faang.user_service.dto.MessageEvent;

public interface MessagePublisher {
    void publish(MessageEvent message);
}
