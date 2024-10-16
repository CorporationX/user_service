package school.faang.user_service.entity.premium;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.model.enums.premium.PremiumPeriod;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PremiumPeriodTest {

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 29, 31, 89, 91, 364, 366, Integer.MAX_VALUE})
    void fromDays_whenInvalidDays_thenCorrect(int days) {
        assertThrows(IllegalArgumentException.class, () -> PremiumPeriod.fromDays(days),
                "No premium period found for %d days".formatted(days));
    }
}