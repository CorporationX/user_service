package school.faang.user_service.dto.types;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum PremiumPeriod {
    MONTH(30, new BigDecimal(10)),
    MONTH_3(90, new BigDecimal(25)),
    YEAR(365, new BigDecimal(80));

    private int days;
    private BigDecimal price;

    PremiumPeriod(int days, BigDecimal price) {
        this.days = days;
        this.price = price;
    }
}
