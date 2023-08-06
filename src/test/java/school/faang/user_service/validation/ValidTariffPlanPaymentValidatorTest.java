package school.faang.user_service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.model.Payment;
import school.faang.user_service.model.TariffPlan;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidTariffPlanPaymentValidatorTest {

    @InjectMocks
    private ValidTariffPlanPaymentValidator subj;

    private PremiumRequestDto obj;


    @BeforeEach
    void setUp() {
        obj = new PremiumRequestDto();
    }

    @Test
    void whenValidTariffPlanPaymentThenTrue() {
        obj.setPayment(new Payment(1L, BigDecimal.TEN, "USD"));
        obj.setTariffPlan(TariffPlan.MONTHLY);

        boolean isValid = subj.isValid(obj, null);

        assertTrue(isValid);
    }

    @Test
    void whenAmountNotMatchTariffPlan() {
        obj.setPayment(new Payment(1L, BigDecimal.TEN, "USD"));
        obj.setTariffPlan(TariffPlan.THREE_MONTHS);

        boolean isValid = subj.isValid(obj, null);

        assertFalse(isValid);
    }

}