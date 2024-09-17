package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.promotion.PromotionValidationException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.EVENT_NOT_FOUND_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_ALREADY_HAS_PROMOTION;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.USER_NOT_OWNER_OF_EVENT;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;

@ExtendWith(MockitoExtension.class)
class PromotionValidationServiceTest {
    private static final long USER_ID = 1;
    private static final long SECOND_USER_ID = 2;
    private static final long EVENT_ID = 1;
    private static final long PROMOTION_ID = 1;
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final int ENOUGH_VIEWS = 2;
    private static final String MESSAGE = "test message";

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private PromotionValidationService promotionValidationService;

    @Test
    @DisplayName("Given non exist user id when check then throw exception")
    void testCheckUserForPromotionNonExistUserId() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionValidationService.checkUserForPromotion(USER_ID))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_PROMOTION, USER_ID);
    }

    @Test
    @DisplayName("Given user with active promotion when check then throw exception")
    void testCheckUserForPromotionActivePromotion() {
        UserPromotion userPromotion = getUserPromotion(PROMOTION_ID, ENOUGH_VIEWS);
        User user = getUser(USER_ID, userPromotion);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> promotionValidationService.checkUserForPromotion(USER_ID))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(USER_ALREADY_HAS_PROMOTION, USER_ID, userPromotion.getNumberOfViews());
    }

    @Test
    @DisplayName("Given non exist event id when check then throw exception")
    void testCheckEventForUserAndPromotionNonExistEventId() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID))
                .isInstanceOf(PromotionNotFoundException.class)
                .hasMessageContaining(EVENT_NOT_FOUND_PROMOTION, EVENT_ID);
    }

    @Test
    @DisplayName("Given not owner user when check then throw exception")
    void testCheckEventForUserAndPromotionUserNotOwner() {
        User user = getUser(USER_ID);
        Event event = getEvent(user);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> promotionValidationService.checkEventForUserAndPromotion(SECOND_USER_ID, EVENT_ID))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(USER_NOT_OWNER_OF_EVENT, SECOND_USER_ID, EVENT_ID);
    }

    @Test
    @DisplayName("Given event with active promotion when check then throw exception")
    void testCheckEventForUserAndPromotionActivePromotion() {
        User user = getUser(USER_ID);
        EventPromotion eventPromotion = getEventPromotion(EVENT_ID, ENOUGH_VIEWS);
        Event event = getEvent(user, eventPromotion);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> promotionValidationService.checkEventForUserAndPromotion(USER_ID, EVENT_ID))
                .isInstanceOf(PromotionValidationException.class)
                .hasMessageContaining(EVENT_ALREADY_HAS_PROMOTION, EVENT_ID, ENOUGH_VIEWS);
    }

    @Test
    @DisplayName("Given unsuccessful payment response when check then throw exception")
    void testCheckPromotionPaymentResponseUnsuccessfulPayment() {
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.FAILED, MESSAGE);

        assertThatThrownBy(() ->
                promotionValidationService.checkPromotionPaymentResponse(paymentResponse, USER_ID, TARIFF, MESSAGE))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining(MESSAGE, TARIFF.getNumberOfViews(), USER_ID, MESSAGE);
    }
}