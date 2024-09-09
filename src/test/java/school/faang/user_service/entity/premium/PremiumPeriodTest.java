package school.faang.user_service.entity.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.PREMIUM_PERIOD_NOT_FOUND;

class PremiumPeriodTest {

    @Test
    @DisplayName("Check all values from days")
    void testFromDaysCheckAllValues() {
        Arrays.stream(PremiumPeriod.values())
                .forEach(period -> assertThat(PremiumPeriod.fromDays(period.getDays())).isEqualTo(period));
    }

    @Test
    @DisplayName("Given wrong days when fromDays then throw exception")
    void testFromDaysWrongDaysExceptions() {
        Arrays.stream(PremiumPeriod.values())
                .forEach(this::assertPeriodException);
    }

    private void assertPeriodException(PremiumPeriod period) {
        assertThatThrownBy(() -> PremiumPeriod.fromDays(period.getDays() + 1))
                .isInstanceOf(PremiumNotFoundException.class)
                .hasMessageContaining(PREMIUM_PERIOD_NOT_FOUND, period.getDays() + 1, PremiumPeriod.daysOptions());
    }
}