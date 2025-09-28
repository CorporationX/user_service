package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    @JsonCreator
    public static RequestStatus fromString(String key) {
        return key == null ? null : RequestStatus.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}