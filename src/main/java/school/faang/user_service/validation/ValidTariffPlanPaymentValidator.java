package school.faang.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.model.TariffPlan;
import school.faang.user_service.validation.annotation.ValidTariffPlanPayment;

import java.math.BigDecimal;

public class ValidTariffPlanPaymentValidator implements ConstraintValidator<ValidTariffPlanPayment, PremiumRequestDto> {

    @Override
    public void initialize(ValidTariffPlanPayment constraintAnnotation) {
    }

    @Override
    public boolean isValid(PremiumRequestDto requestDto, ConstraintValidatorContext context) {
        if (requestDto.getPayment().getPaymentNumber() == null
            || requestDto.getPayment().getAmount() == null
            || requestDto.getPayment().getCurrency() == null) {
            return true;
        }
        TariffPlan tariffPlan = requestDto.getTariffPlan();
        BigDecimal amount = requestDto.getPayment().getAmount();
        return tariffPlan.getPrice().equals(amount);
    }
}
