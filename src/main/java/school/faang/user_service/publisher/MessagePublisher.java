package school.faang.user_service.publisher;

import school.faang.user_service.dto.EventRegistrationNotificationDto;

public interface MessagePublisher {
    void publish(EventRegistrationNotificationDto message);
}
