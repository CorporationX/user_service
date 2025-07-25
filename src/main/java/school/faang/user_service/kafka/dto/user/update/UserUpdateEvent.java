package school.faang.user_service.kafka.dto.user.update;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserUpdateEvent {
    @JsonIgnore
    String getType();

    long getId();
}
