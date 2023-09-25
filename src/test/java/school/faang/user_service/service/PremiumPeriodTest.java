package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import school.faang.user_service.entity.premium.PremiumPeriod;

public class PremiumPeriodTest {
    PremiumPeriod period;

    @ParameterizedTest
    @CsvSource({
            "30, ONE_MONTH",
            "31, ONE_MONTH",
            "90, THREE_MONTHS",
            "91, THREE_MONTHS",
            "365, ONE_YEAR",
            "366, ONE_YEAR"
    })
    void testFromDaysWithValidInput(int days, PremiumPeriod expectedPeriod) {
        Assertions.assertEquals(expectedPeriod, PremiumPeriod.fromDays(days));
    }

    @ParameterizedTest
    @CsvSource({
            "29",
            "0",
            "-10"
    })
    public void testFromDaysWithInvalidInput(int days) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PremiumPeriod.fromDays(days)
        );
    }
}


