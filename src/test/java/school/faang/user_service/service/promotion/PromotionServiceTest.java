package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.util.PromotionBuilder;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvents;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUsers;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final long PROMOTION_ID = 1;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final int NOT_ENOUGH_VIEWS = 0;
    private static final String MESSAGE = "test message";
    private static final int NUMBER_OF_USERS = 3;
    private static final int NUMBER_OF_EVENTS = 3;
    private static final int LIMIT = 10;
    private static final int OFFSET = 0;

    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private EventPromotionRepository eventPromotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PromotionTaskService promotionTaskService;

    @Mock
    private PromotionCheckService promotionCheckService;

    @Mock
    private PaymentService paymentService;

    @Spy
    private PromotionBuilder promotionBuilder;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    @DisplayName("Given not valid promotion when check then delete promotion")
    void testBuyPromotionDeleteNotValidPromotion() {
        UserPromotion userPromotion = getUserPromotion(PROMOTION_ID, NOT_ENOUGH_VIEWS);
        User user = getUser(USER_ID, userPromotion);
        PaymentResponse paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(promotionCheckService.checkUserForPromotion(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyPromotion(USER_ID, TARIFF);

        verify(userPromotionRepository).delete(userPromotion);
    }

    @Test
    @DisplayName("Successful buy promotion")
    void testBuyPromotionSuccessful() {
        User user = getUser(USER_ID, null);
        PaymentResponse paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(promotionCheckService.checkUserForPromotion(USER_ID)).thenReturn(user);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyPromotion(USER_ID, TARIFF);

        verify(userPromotionRepository).save(any(UserPromotion.class));
    }

    @Test
    @DisplayName("Given not valid event promotion when check then delete promotion")
    void testBuyEventPromotionDeletePromotion() {
        User user = getUser(USER_ID);
        EventPromotion eventPromotion = getEventPromotion(EVENT_ID, NOT_ENOUGH_VIEWS);
        Event event = getEvent(user, eventPromotion);
        PaymentResponse paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(promotionCheckService.checkEventForUserAndPromotion(USER_ID, EVENT_ID)).thenReturn(event);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyEventPromotion(USER_ID, EVENT_ID, TARIFF);

        verify(eventPromotionRepository).delete(eventPromotion);
    }

    @Test
    @DisplayName("Buy event promotion successful")
    void testBuyEventPromotionSuccessful() {
        User user = getUser(USER_ID);
        Event event = getEvent(user, null);
        PaymentResponse paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(promotionCheckService.checkEventForUserAndPromotion(USER_ID, EVENT_ID)).thenReturn(event);
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyEventPromotion(USER_ID, EVENT_ID, TARIFF);

        verify(eventPromotionRepository).save(any(EventPromotion.class));
    }

    @Test
    @DisplayName("Get promoted users per page success")
    void testGetPromotedUsersBeforeAllPerPageSuccessful() throws InterruptedException {
        List<User> users = getUsers(NUMBER_OF_USERS);
        CountDownLatch latch = new CountDownLatch(1);
        when(userRepository.findAllSortedByPromotedUsersPerPage(LIMIT, OFFSET)).thenReturn(users);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(promotionTaskService).decrementUserPromotionViews(any());
        promotionService.getPromotedUsersBeforeAllPerPage(LIMIT, OFFSET);
        latch.await(5, TimeUnit.SECONDS);

        verify(promotionTaskService).decrementUserPromotionViews(users);
    }

    @Test
    @DisplayName("Get promoted events per page success")
    void testGetPromotedEventsBeforeAllPerPageSuccessful() throws InterruptedException {
        List<Event> events = getEvents(NUMBER_OF_EVENTS);
        CountDownLatch latch = new CountDownLatch(1);
        when(eventRepository.findAllSortedByPromotedEventsPerPage(LIMIT, OFFSET)).thenReturn(events);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(promotionTaskService).decrementEventPromotionViews(any());
        promotionService.getPromotedEventsBeforeAllPerPage(LIMIT, OFFSET);
        latch.await(5, TimeUnit.SECONDS);

        verify(promotionTaskService).decrementEventPromotionViews(events);
    }
}