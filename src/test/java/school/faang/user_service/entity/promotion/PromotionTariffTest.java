package school.faang.user_service.entity.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.PROMOTION_NOT_FOUND;


class PromotionTariffTest {

    @Test
    @DisplayName("Check all values from views")
    void testFromViewsAllValues() {
        Arrays.stream(PromotionTariff.values())
                .forEach(tariff -> assertThat(PromotionTariff.fromViews(tariff.getNumberOfViews()))
                        .isEqualTo(tariff));
    }

    @Test
    @DisplayName("Given wrong views when fromViews then throw exception")
    void testFromViewsWrongViewsException() {
        Arrays.stream(PromotionTariff.values())
                .forEach(this::assertTariffException);
    }

    private void assertTariffException(PromotionTariff tariff) {
        assertThatThrownBy(() -> PromotionTariff.fromViews(tariff.getNumberOfViews() + 1))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessageContaining(PROMOTION_NOT_FOUND, tariff.getNumberOfViews() + 1,
                        PromotionTariff.viewsOption());
    }

}