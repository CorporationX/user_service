package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.Currency;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionalPlan;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotiom.PromotionRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PromotionValidator;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PromotionMapper promotionMapper;
    private final UserMapper userMapper;
    private final PromotionValidator promotionValidator;
    private final PaymentValidator paymentValidator;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public PromotionDto promoteUser(long userId, String promotionalPlanName, String currencyName) {
        PromotionalPlan promotionalPlan = PromotionalPlan.getFromName(promotionalPlanName);
        Currency currency = Currency.getFromName(currencyName);
        promotionValidator.validateUserAlreadyHasPromotion(userId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(createPaymentRequest(promotionalPlan.getCost(), currency));
        paymentValidator.validatePaymentSuccess(paymentResponse);
        User user =
            userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with ID: %d does not exist.", userId)));
        Promotion promotion = createPromotionForUser(user, promotionalPlan);
        return promotionMapper.toDto(promotion);
    }

    @Transactional
    public PromotionDto promoteEvent(long eventId, String promotionalPlanName, String currencyName) {
        PromotionalPlan promotionalPlan = PromotionalPlan.getFromName(promotionalPlanName);
        Currency currency = Currency.getFromName(currencyName);
        promotionValidator.validateEventAlreadyHasPromotion(eventId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(createPaymentRequest(promotionalPlan.getCost(), currency));
        paymentValidator.validatePaymentSuccess(paymentResponse);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format("Event with ID: %d does not exist.", eventId)));
        Promotion promotion = createPromotionForEvent(event, promotionalPlan);
        return promotionMapper.toDto(promotion);
    }

    @Transactional
    public List<UserDto> showPromotedUsersFirst() {
        List<User> regularUsers = userRepository.findAll();
        List<User> promotedUser = userRepository.findPromotedUsers();
        updatePromotionImpressions(promotedUser);
        List<User> combinedUsers = combineUsers(regularUsers, promotedUser);
        return userMapper.usersToUserDTOs(combinedUsers);
    }

    private Promotion createPromotionForUser(User user, PromotionalPlan promotionalPlan) {
        LocalDateTime startDate = LocalDateTime.now();
        Promotion promotion =
            Promotion.builder().user(user).promotionalPlan(promotionalPlan).impressions(promotionalPlan.getImpressions()).startDate(startDate).endDate(startDate.plusDays(promotionalPlan.getDurationInDays())).build();
        promotionRepository.save(promotion);
        userRepository.save(user);
        return promotion;
    }

    private Promotion createPromotionForEvent(Event event, PromotionalPlan promotionalPlan) {
        LocalDateTime startDate = LocalDateTime.now();
        Promotion promotion =
            Promotion.builder().event(event).promotionalPlan(promotionalPlan).impressions(promotionalPlan.getImpressions()).startDate(startDate).endDate(startDate.plusDays(promotionalPlan.getDurationInDays())).build();
        promotionRepository.save(promotion);
        eventRepository.save(event);
        return promotion;
    }

    private PaymentRequest createPaymentRequest(double amount, Currency currency) {
        return PaymentRequest.builder()
            .amount(amount)
            .currency(currency)
            .build();
    }

    private void updatePromotionImpressions(List<User> promotedUsers) {
        promotedUsers.forEach(user -> {
            Promotion promotion = user.getPromotion();
            promotion.setImpressions(promotion.getImpressions() - 1);
            promotionRepository.save(promotion);
        });
    }

    private List<User> combineUsers(List<User> regularUsers, List<User> promotedUsers) {
        Set<User> combinedUsersSet = new LinkedHashSet<>(promotedUsers);
        combinedUsersSet.addAll(regularUsers);
        return combinedUsersSet.stream().toList();
    }
}
