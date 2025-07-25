package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.dto.user.update.UserUpdateEvent;

public interface UserUpdateProducer {
    void onUserUpdate(UserUpdateEvent dto);
}
