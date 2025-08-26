package school.faang.user_service.kafka.producer;

import school.faang.avro.user.UserAddSkills;
import school.faang.avro.user.UserUpdate;

public interface UserUpdateProducer {
    void onUserUpdate(UserUpdate dto);

    void onUserAddSkills(UserAddSkills dto);
}
