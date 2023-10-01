package school.faang.user_service.entity.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PremiumPeriodTest {

    @Test
    void testFromDays_shouldReturnMonthly() {
        PremiumPeriod period = PremiumPeriod.fromDays(30);
        assertEquals(PremiumPeriod.MONTHLY, PremiumPeriod.fromDays(30));
    }

    @Test
    void testFromDays_shouldReturnQuarterly() {
        PremiumPeriod period = PremiumPeriod.fromDays(90);
        assertEquals(PremiumPeriod.QUARTERLY, PremiumPeriod.fromDays(90));
    }

    @Test
    void testFromDays_shouldReturnAnnual() {
        PremiumPeriod period = PremiumPeriod.fromDays(365);
        assertEquals(PremiumPeriod.ANNUAL, PremiumPeriod.fromDays(365));
    }
}