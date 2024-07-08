package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PremiumAlreadyPurchasedException extends IllegalArgumentException {
    public PremiumAlreadyPurchasedException(long userId) {
        super(String.format("User with ID %d already has a premium subscription.", userId));
        log.error("User with ID {} already has a premium subscription.", userId, this);
    }
}
