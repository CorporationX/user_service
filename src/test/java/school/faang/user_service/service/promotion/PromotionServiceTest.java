package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.util.PromotionBuilder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND_WHEN_BUYING_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_NOT_FOUND_PROMOTION;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveEventPromotions;
import static school.faang.user_service.util.promotion.PromotionFabric.buildActiveUserPromotions;
import static school.faang.user_service.util.promotion.PromotionFabric.buildEventsWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.buildUsersWithActivePromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvents;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUsers;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final long NOT_EXIST_USER_ID = 2;
    private static final long NOT_EXIST_EVENT_ID = 2;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final String MESSAGE = "test message";
    private static final int NUMBER_OF_USERS = 3;
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
    private PaymentService paymentService;

    @Mock
    private PromotionTaskService promotionTaskService;

    @Mock
    private PromotionValidationService promotionValidationService;

    @Spy
    private PromotionBuilder promotionBuilder;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    @DisplayName("Given not exist user id when find then throw exception")
    void testBuyPromotionUserNotFoundException() {
        when(userRepository.findById(NOT_EXIST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionService.buyPromotion(NOT_EXIST_USER_ID, TARIFF))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_WHEN_BUYING_PROMOTION, NOT_EXIST_USER_ID);
    }

    @Test
    @DisplayName("Successful buy user promotion")
    void testBuyPromotionSuccessful() {
        User user = getUser(USER_ID);
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyPromotion(USER_ID, TARIFF);

        verify(userPromotionRepository).save(any(UserPromotion.class));
    }

    @Test
    @DisplayName("Given not exist event id when find then throw exception")
    void testBuyEventPromotionEventNotFound() {
        when(eventRepository.findById(NOT_EXIST_EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionService.buyEventPromotion(USER_ID, NOT_EXIST_EVENT_ID, TARIFF))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessageContaining(EVENT_NOT_FOUND_PROMOTION, NOT_EXIST_EVENT_ID);
    }

    @Test
    @DisplayName("Buy event promotion successful")
    void testBuyEventPromotionSuccessful() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(paymentService.sendPayment(TARIFF)).thenReturn(paymentResponse);
        promotionService.buyEventPromotion(USER_ID, EVENT_ID, TARIFF);

        verify(eventPromotionRepository).save(any(EventPromotion.class));
    }

    @Test
    @DisplayName("Get promoted users per page success")
    void testGetPromotedUsersBeforeAllPerPageSuccessful() {
        List<User> users = buildUsersWithActivePromotion(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = buildActiveUserPromotions(NUMBER_OF_USERS);
        when(userRepository.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(promotionValidationService.getActiveUserPromotions(users)).thenReturn(activePromotions);

        assertThat(promotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(users);
        verify(promotionTaskService).decrementUserPromotionViews(activePromotions);
    }

    @Test
    @DisplayName("Get promoted users per page with no active promotion when check then no call decrement")
    void testGetPromotedUsersBeforeAllPerPageEmptyActivePromotions() {
        List<User> users = getUsers(NUMBER_OF_USERS);
        List<UserPromotion> activePromotions = List.of();
        when(userRepository.findAllSortedByPromotedUsersPerPage(OFFSET, LIMIT)).thenReturn(users);
        when(promotionValidationService.getActiveUserPromotions(users)).thenReturn(activePromotions);

        assertThat(promotionService.getPromotedUsersBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(users);
        verify(promotionTaskService, never()).decrementUserPromotionViews(activePromotions);
    }

    @Test
    @DisplayName("Get promoted events per page success")
    void testGetPromotedEventsBeforeAllPerPageSuccessful() {
        List<Event> events = buildEventsWithActivePromotion(NUMBER_OF_USERS);
        List<EventPromotion> eventPromotions = buildActiveEventPromotions(NUMBER_OF_USERS);
        when(eventRepository.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(promotionValidationService.getActiveEventPromotions(events)).thenReturn(eventPromotions);

        assertThat(promotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(events);
        verify(promotionTaskService).decrementEventPromotionViews(eventPromotions);
    }

    @Test
    @DisplayName("Get promoted events per page with no active promotion when check then no call decrement")
    void testGetPromotedEventsBeforeAllPerPageEmptyActivePromotions() {
        List<Event> events = getEvents(NUMBER_OF_USERS);
        List<EventPromotion> activePromotions = List.of();
        when(eventRepository.findAllSortedByPromotedEventsPerPage(OFFSET, LIMIT)).thenReturn(events);
        when(promotionValidationService.getActiveEventPromotions(events)).thenReturn(activePromotions);

        assertThat(promotionService.getPromotedEventsBeforeAllPerPage(OFFSET, LIMIT))
                .isEqualTo(events);
        verify(promotionTaskService, never()).decrementEventPromotionViews(activePromotions);
    }
}