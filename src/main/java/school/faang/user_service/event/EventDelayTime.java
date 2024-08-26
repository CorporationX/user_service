package school.faang.user_service.event;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum EventDelayTime {

    NOW(0, TimeUnit.MILLISECONDS),
    TEN_MINUTES(10, TimeUnit.MINUTES),
    FIVE_MINUTES(5, TimeUnit.MINUTES);

    private final int value;
    private final TimeUnit timeUnit;

    EventDelayTime(int value, TimeUnit timeUnit) {
        this.value = value;
        this.timeUnit = timeUnit;
    }

    public Long getTime() {
        return timeUnit.toMillis(value);
    }

}
