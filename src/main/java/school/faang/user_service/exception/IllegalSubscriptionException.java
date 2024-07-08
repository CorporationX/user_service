package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IllegalSubscriptionException extends IllegalArgumentException {
    public IllegalSubscriptionException(int days) {
        super(String.format("No subscription found for %d days.", days));
        log.error("No subscription found for {} days.", days, this);
    }
}
