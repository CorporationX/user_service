package school.faang.user_service.entity.promotion;

import school.faang.user_service.exception.IllegalEntityException;
import school.faang.user_service.exception.IllegalPromotionException;

import java.util.Arrays;

public enum AudienceReach {
    LOCAL,
    NATIONAL,
    REGIONAL,
    INTERNATIONAL;

    public static AudienceReach fromName(String audienceReachName) {
        return Arrays.stream(AudienceReach.values())
            .filter(audienceReach -> audienceReach.name().equals(audienceReachName.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new IllegalEntityException(String.format("Invalid audience reach: %s", audienceReachName)));
    }
}