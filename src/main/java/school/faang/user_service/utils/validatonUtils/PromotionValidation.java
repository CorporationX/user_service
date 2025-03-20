package school.faang.user_service.utils.validatonUtils;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.model.promotion.PromotionPriority;
import school.faang.user_service.model.promotion.event.EventPromotionType;
import school.faang.user_service.model.promotion.user.UserPromotionType;

import java.time.LocalDateTime;

@Slf4j
public class PromotionValidation {
    public static final String DATE_CANNOT_BE_NULL = "Start date and end date cannot be null";
    public static final String START_DATE_CANNOT_BE_AFTER_END_DATE = "Start date cannot be after end date";
    public static final String USER_ID_CANNOT_BE_NULL = "UserId can't be null";
    public static final String EVENT_ID_CANNOT_BE_NULL = "EventId can't be null";
    public static final String USER_PROMOTION_TYPE_CANNOT_BE_NULL = "UserPromotionType can't be null";
    public static final String EVENT_PROMOTION_TYPE_CANNOT_BE_NULL = "EventPromotionType can't be null";
    public static final String PROMOTION_PRIORITY_CANNOT_BE_NULL = "PromotionPriority can't be null";

    public static void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            log.error(DATE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(DATE_CANNOT_BE_NULL);
        }
        if (startDate.isAfter(endDate)) {
            log.error(START_DATE_CANNOT_BE_AFTER_END_DATE);
            throw new IllegalArgumentException(START_DATE_CANNOT_BE_AFTER_END_DATE);
        }
    }

    public static void validateUserId(Long userId) {
        if (userId == null) {
            log.error(USER_ID_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_ID_CANNOT_BE_NULL);
        }
    }

    public static void validateEventId(Long eventId) {
        if (eventId == null) {
            log.error(EVENT_ID_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_ID_CANNOT_BE_NULL);
        }
    }

    private static void validateUserPromotionType(UserPromotionType userPromotionType) {
        if (userPromotionType == null) {
            log.error(USER_PROMOTION_TYPE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_PROMOTION_TYPE_CANNOT_BE_NULL);
        }
    }

    private static void validateEventPromotionType(EventPromotionType userPromotionType) {
        if (userPromotionType == null) {
            log.error(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(EVENT_PROMOTION_TYPE_CANNOT_BE_NULL);
        }
    }

    private static void validatePromotionPriority(PromotionPriority promotionPriority) {
        if (promotionPriority == null) {
            log.error(PROMOTION_PRIORITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(PROMOTION_PRIORITY_CANNOT_BE_NULL);
        }
    }

    public static void validateUserPromotion(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                             UserPromotionType promotionType, PromotionPriority promotionPriority) {
        validateUserId(userId);
        validateDates(startDate, endDate);
        validateUserPromotionType(promotionType);
        validatePromotionPriority(promotionPriority);
    }

    public static void validateEventPromotion(Long eventId, LocalDateTime startDate, LocalDateTime endDate,
                                              EventPromotionType promotionType, PromotionPriority promotionPriority) {
        validateEventId(eventId);
        validateDates(startDate, endDate);
        validateEventPromotionType(promotionType);
        validatePromotionPriority(promotionPriority);
    }
}
