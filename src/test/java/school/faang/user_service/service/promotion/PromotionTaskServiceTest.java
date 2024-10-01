package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.batch.EventPromotionRepositoryBatch;
import school.faang.user_service.repository.promotion.batch.UserPromotionRepositoryBatch;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Given user promotion and increment user promotion views for batch decrement")
    void testDecrementUserPromotionViewsIncrementViews() {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));
        promotionTaskService.decrementUserPromotionViews(userPromotions);

        Map<Long, Integer> userPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "userPromotionViews");
        assertThat(userPromotionViews)
                .isNotNull();
        assertThat(userPromotionViews.get(PROMOTION_ID))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Given empty user promo views when check then don't execute decrement")
    void testExecuteUserPromotionViewsDecrementEmptyPromotionViews() throws SQLException {
        promotionTaskService.executeUserPromotionViewsDecrement();

        verify(userPromotionRepositoryBatch, never()).updateUserPromotions(anyMap());
    }

    @Test
    @DisplayName("Given already running user promo decrement process when check then don't execute decrement")
    void testExecuteUserPromotionViewsDecrementDecrementAlreadyRunning() throws SQLException {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));
        ReflectionTestUtils.setField(promotionTaskService, "isUserViewsDecrementRunning", new AtomicBoolean(true));
        promotionTaskService.decrementUserPromotionViews(userPromotions);
        promotionTaskService.executeUserPromotionViewsDecrement();

        verify(userPromotionRepositoryBatch, never()).updateUserPromotions(anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Execute user promotion views decrement successful ")
    void testExecuteUserPromotionViewsDecrementSuccessful() throws SQLException {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));

        promotionTaskService.decrementUserPromotionViews(userPromotions);
        promotionTaskService.executeUserPromotionViewsDecrement();
        Map<Long, Integer> userPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "userPromotionViews");

        assertThat(userPromotionViews).isEmpty();
        verify(userPromotionRepositoryBatch).updateUserPromotions(anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("When updating user promotion views throw exception then return the show to the list")
    void testExecuteUserPromotionViewsDecrementSQLException() throws SQLException {
        List<UserPromotion> userPromotions = List.of(buildActiveUserPromotion(PROMOTION_ID));
        doThrow(new SQLException()).when(userPromotionRepositoryBatch).updateUserPromotions(anyMap());

        promotionTaskService.decrementUserPromotionViews(userPromotions);
        promotionTaskService.executeUserPromotionViewsDecrement();
        Map<Long, Integer> userPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "userPromotionViews");

        assertThat(userPromotionViews).isNotEmpty();
        verify(userPromotionRepositoryBatch).updateUserPromotions(anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Given event promotion and increment user promotion views for batch decrement")
    void testDecrementEventPromotionViewsIncrementViews() {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));
        promotionTaskService.decrementEventPromotionViews(eventPromotions);

        Map<Long, Integer> eventPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "eventPromotionViews");
        assertThat(eventPromotionViews)
                .isNotNull();
        assertThat(eventPromotionViews.get(PROMOTION_ID))
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Given empty event promo views when check then don't execute decrement")
    void testExecuteEventPromotionViewsDecrementEmptyPromotionViews() throws SQLException {
        promotionTaskService.executeEventPromotionViewsDecrement();

        verify(eventPromotionRepositoryBatch, never()).updateEventPromotions(anyMap());
    }

    @Test
    @DisplayName("Given already running event promo decrement process when check then don't execute decrement")
    void testExecuteEventPromotionViewsDecrementDecrementAlreadyRunning() throws SQLException {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));
        ReflectionTestUtils.setField(promotionTaskService, "isEventViewsDecrementRunning", new AtomicBoolean(true));
        promotionTaskService.decrementEventPromotionViews(eventPromotions);
        promotionTaskService.executeEventPromotionViewsDecrement();

        verify(eventPromotionRepositoryBatch, never()).updateEventPromotions(anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Execute event promotion views decrement successful ")
    void testExecuteEventPromotionViewsDecrementSuccessful() throws SQLException {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));

        promotionTaskService.decrementEventPromotionViews(eventPromotions);
        promotionTaskService.executeEventPromotionViewsDecrement();
        Map<Long, Integer> eventPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "eventPromotionViews");

        assertThat(eventPromotionViews).isEmpty();
        verify(eventPromotionRepositoryBatch).updateEventPromotions(anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("When updating event promotion views throw exception then return the show to the list")
    void testExecuteEventPromotionViewsDecrementSQLException() throws SQLException {
        List<EventPromotion> eventPromotions = List.of(buildActiveEventPromotion(PROMOTION_ID));
        doThrow(new SQLException()).when(eventPromotionRepositoryBatch).updateEventPromotions(anyMap());

        promotionTaskService.decrementEventPromotionViews(eventPromotions);
        promotionTaskService.executeEventPromotionViewsDecrement();
        Map<Long, Integer> eventPromotionViews =
                (Map<Long, Integer>) ReflectionTestUtils.getField(promotionTaskService, "eventPromotionViews");

        assertThat(eventPromotionViews).isNotEmpty();
        verify(eventPromotionRepositoryBatch).updateEventPromotions(anyMap());
    }
}