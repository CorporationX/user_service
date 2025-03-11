package school.faang.user_service.entity.contact;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PregerredContactNotification {
    EMAIL("EMAIL"),
    SMS("SMS");

    private final String value;

    PregerredContactNotification(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PregerredContactNotification fromValue(String value) {
        for (PregerredContactNotification type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
