package school.faang.user_service.entity.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.model.enums.PremiumPeriod;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PremiumPeriodTest {

    @Test
    @DisplayName("Should return correct periods for valid days")
    public void testFromDays_ValidDays() {
        Arrays.stream(PremiumPeriod.values())
                .forEach(period -> assertEquals(period, PremiumPeriod.fromDays(period.getDays())));
    }

    @Test
    @DisplayName("Should throw exception for invalid days")
    public void testFromDays_InvalidDays() {
        int days = 100;

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                PremiumPeriod.fromDays(days));

        assertEquals("Invalid premium period", exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly initialize fields")
    void testInitializeFields() {

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
