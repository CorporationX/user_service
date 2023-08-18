package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.Payment;
import school.faang.user_service.model.TariffPlan;
import school.faang.user_service.validation.annotation.ValidTariffPlanPayment;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidTariffPlanPayment
public class PremiumRequestDto {

    @NotNull
    private Payment payment;

    @NotNull
    private TariffPlan tariffPlan;
}
