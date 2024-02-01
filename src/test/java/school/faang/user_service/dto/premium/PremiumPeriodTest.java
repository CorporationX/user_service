package school.faang.user_service.dto.premium;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.*;

class PremiumPeriodTest {
    @Test
    void fromDays_whenValidDays_thenReturnsCorrectPeriod() {
        assertAll(
                () -> assertEquals(PremiumPeriod.ONE_MONTH, PremiumPeriod.fromDays(30)),
                () -> assertEquals(PremiumPeriod.THREE_MONTHS, PremiumPeriod.fromDays(90)),
                () -> assertEquals(PremiumPeriod.ONE_YEAR, PremiumPeriod.fromDays(365))
        );
    }

    @Test
    void fromDays_whenInvalidDays_thenThrowsException() {
        int invalidDays = 15;
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> PremiumPeriod.fromDays(invalidDays));

        assertEquals("Нет подписки с таким периодом", dataValidationException.getMessage());
    }

    @Test
    void testEnumValues() {
        assertAll(
                () -> assertEquals(30, PremiumPeriod.ONE_MONTH.getDays()),
                () -> assertEquals(10, PremiumPeriod.ONE_MONTH.getPrice()),

                () -> assertEquals(90, PremiumPeriod.THREE_MONTHS.getDays()),
                () -> assertEquals(25, PremiumPeriod.THREE_MONTHS.getPrice()),

                () -> assertEquals(365, PremiumPeriod.ONE_YEAR.getDays()),
                () -> assertEquals(80, PremiumPeriod.ONE_YEAR.getPrice())
        );
    }
}