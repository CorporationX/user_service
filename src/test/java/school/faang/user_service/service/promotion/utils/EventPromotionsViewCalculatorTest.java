package school.faang.user_service.service.promotion.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventPromotionsViewCalculatorTest {
    private EventPromotionsViewCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new EventPromotionsViewCalculator();
        ReflectionTestUtils.setField(calculator, "vipCoefficient", 0.1);
        ReflectionTestUtils.setField(calculator, "goldCoefficient", 0.2);
        ReflectionTestUtils.setField(calculator, "plusCoefficient", 0.3);
    }

    @Test
    void calculatePromotedViews_NormalCase() {
        int count = 100;
        Map<Plan, Integer> result = calculator.calculatePromotedViews(count);

        assertEquals(4, result.size());

        assertEquals(10, result.get(Plan.VIP));
        assertEquals(20, result.get(Plan.GOLD));
        assertEquals(30, result.get(Plan.PLUS));
        assertEquals(40, result.get(null));

        int sum = result.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(count, sum);
    }

    @Test
    void calculatePromotedViews_RoundingDownBehavior() {
        ReflectionTestUtils.setField(calculator, "vipCoefficient", 0.33);
        ReflectionTestUtils.setField(calculator, "goldCoefficient", 0.33);
        ReflectionTestUtils.setField(calculator, "plusCoefficient", 0.33);

        int count = 10;
        Map<Plan, Integer> result = calculator.calculatePromotedViews(count);

        assertEquals(3, result.get(Plan.VIP));
        assertEquals(3, result.get(Plan.GOLD));
        assertEquals(3, result.get(Plan.PLUS));
        assertEquals(1, result.get(null));

        int sum = result.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(count, sum);
    }

    @Test
    void calculatePromotedViews_ZeroCountReturnsAllZeros() {
        int count = 0;
        Map<Plan, Integer> result = calculator.calculatePromotedViews(count);

        assertEquals(0, result.get(Plan.VIP));
        assertEquals(0, result.get(Plan.GOLD));
        assertEquals(0, result.get(Plan.PLUS));
        assertEquals(0, result.get(null));

        int sum = result.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(0, sum);
    }

    @Test
    void calculatePromotedViews_AllCoefficientsZero() {
        ReflectionTestUtils.setField(calculator, "vipCoefficient", 0.0);
        ReflectionTestUtils.setField(calculator, "goldCoefficient", 0.0);
        ReflectionTestUtils.setField(calculator, "plusCoefficient", 0.0);

        int count = 50;
        Map<Plan, Integer> result = calculator.calculatePromotedViews(count);

        assertEquals(0, result.get(Plan.VIP));
        assertEquals(0, result.get(Plan.GOLD));
        assertEquals(0, result.get(Plan.PLUS));
        assertEquals(50, result.get(null));

        int sum = result.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(count, sum);
    }
}