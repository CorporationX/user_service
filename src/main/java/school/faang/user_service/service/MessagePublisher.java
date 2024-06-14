package school.faang.user_service.service;

import school.faang.user_service.dto.MessageEvent;

public interface MessagePublisher {
    void publish(MessageEvent message);
}
