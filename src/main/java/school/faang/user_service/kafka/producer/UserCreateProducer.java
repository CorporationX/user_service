package school.faang.user_service.kafka.producer;


import school.faang.avro.user.UserCreate;

public interface UserCreateProducer {
    void onUserCreate(UserCreate userCreate);
}
