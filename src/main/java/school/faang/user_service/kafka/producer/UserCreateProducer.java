package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.dto.user.UserCreate;

public interface UserCreateProducer {
    void onUserCreate(UserCreate userCreate);
}
