package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.EventPromotionRepositoryBatch;
import school.faang.user_service.repository.promotion.UserPromotionRepositoryBatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveEventPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveUserPromotion;

@ExtendWith(MockitoExtension.class)
class PromotionTaskServiceTest {
    private static final long PROMOTION_ID = 1;

    @Mock
    private UserPromotionRepositoryBatch userPromotionRepositoryBatch;

    @Mock
    private EventPromotionRepositoryBatch eventPromotionRepositoryBatch;

    @InjectMocks
    private PromotionTaskService promotionTaskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(promotionTaskService, "threadPoolSize", 1);
    }

    @Test
    @DisplayName("Given user promotion and increment user promotion views for batch decrement")
    void testDecrementUserPromotionViewsIncrementViews() {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));
        promotionTaskService.decrementUserPromotionViews(userPromotions);

        Map<Long, Integer> userPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "userPromotionViews");
        assertThat(userPromotionViews.get(PROMOTION_ID))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Given false isUserViewDecrementRunning when check then schedule decrement method")
    void testDecrementUserPromotionViewsScheduleDecrement() throws InterruptedException {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(userPromotionRepositoryBatch).updateUserPromotions(anyMap());
        promotionTaskService.decrementUserPromotionViews(userPromotions);

        latch.await(5, TimeUnit.SECONDS);
        verify(userPromotionRepositoryBatch).updateUserPromotions(anyMap());
    }

    @Test
    @DisplayName("Given event promotion and increment user promotion views for batch decrement")
    void testDecrementEventPromotionViewsIncrementViews() {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));
        promotionTaskService.decrementEventPromotionViews(eventPromotions);

        Map<Long, Integer> eventPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "eventPromotionViews");
        assertThat(eventPromotionViews.get(PROMOTION_ID))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Given false isEventViewDecrementRunning when check then schedule decrement method")
    void testDecrementEventPromotionViewsScheduleDecrement() throws InterruptedException {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(eventPromotionRepositoryBatch).updateEventPromotions(anyMap());
        promotionTaskService.decrementEventPromotionViews(eventPromotions);

        latch.await(5, TimeUnit.SECONDS);
        verify(eventPromotionRepositoryBatch).updateEventPromotions(anyMap());
    }
}