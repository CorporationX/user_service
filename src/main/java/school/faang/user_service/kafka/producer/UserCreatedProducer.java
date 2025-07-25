package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.dto.user.UserCreated;

public interface UserCreatedProducer {
    void onUserCreate(UserCreated userCreated);
}
