package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PromotionAlreadyPurchasedException extends IllegalArgumentException {
    public PromotionAlreadyPurchasedException(long userId) {
        super(String.format("User with ID %d already has a promotion.", userId));
        log.error("User with ID {} already has a promotion.", userId, this);
    }
}
