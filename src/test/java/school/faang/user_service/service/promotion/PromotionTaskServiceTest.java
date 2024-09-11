package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvents;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUsers;

@ExtendWith(MockitoExtension.class)
class PromotionTaskServiceTest {
    private static final int NUMBER_OF_USERS = 3;
    private static final int ENOUGH_VIEWS = 2;
    private static final int NOT_ENOUGH_VIEWS = 1;
    private static final long PROMOTION_ID = 1;

    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @InjectMocks
    private PromotionTaskService promotionTaskService;

    @Test
    @DisplayName("Given user promotion not enough views when check then delete")
    void testDecrementUserPromotionViewsDeletePromotion() {
        UserPromotion userPromotion = getUserPromotion(PROMOTION_ID, NOT_ENOUGH_VIEWS);
        List<User> users = getUsers(NUMBER_OF_USERS, userPromotion);
        when(userPromotionRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(userPromotion));

        promotionTaskService.decrementUserPromotionViews(users);
        verify(userPromotionRepository, times(users.size())).delete(userPromotion);
    }

    @Test
    @DisplayName("Given user promotion enough views when check then decrement")
    void testDecrementUserPromotionViewsSuccessful() {
        UserPromotion userPromotion = getUserPromotion(PROMOTION_ID, ENOUGH_VIEWS);
        List<User> users = getUsers(NUMBER_OF_USERS, userPromotion);
        when(userPromotionRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(userPromotion));

        promotionTaskService.decrementUserPromotionViews(users);
        verify(userPromotionRepository, times(users.size())).decrementPromotionViews(userPromotion.getId());
    }

    @Test
    @DisplayName("Given event promotion not enough views when check then delete")
    void testDecrementEventPromotionViewsDeletePromotion() {
        EventPromotion eventPromotion = getEventPromotion(PROMOTION_ID, NOT_ENOUGH_VIEWS);
        List<Event> events = getEvents(NUMBER_OF_USERS, eventPromotion);
        when(eventPromotionRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(eventPromotion));

        promotionTaskService.decrementEventPromotionViews(events);
        verify(eventPromotionRepository, times(events.size())).delete(eventPromotion);
    }

    @Test
    @DisplayName("Given event promotion enough views when check then decrement")
    void testDecrementEventPromotionViewsSuccessful() {
        EventPromotion eventPromotion = getEventPromotion(PROMOTION_ID, ENOUGH_VIEWS);
        List<Event> events = getEvents(NUMBER_OF_USERS, eventPromotion);
        when(eventPromotionRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(eventPromotion));

        promotionTaskService.decrementEventPromotionViews(events);
        verify(eventPromotionRepository, times(events.size())).decrementPromotionViews(eventPromotion.getId());
    }
}