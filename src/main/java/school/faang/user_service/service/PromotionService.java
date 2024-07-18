package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionalPlan;
import school.faang.user_service.exception.*;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotiom.PromotionRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PromotionValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public PromotionDto promoteUser(long userId, PromotionalPlan promotionalPlan) {
        promotionValidator.validateUserAlreadyHasPromotion(userId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new BigDecimal(promotionalPlan.getCost()),
            Currency.USD
        );
        paymentValidator.validatePaymentSuccess(paymentResponse);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Event with ID: %d does not exist.", userId)));
        Promotion promotion = createPromotionForUser(user, promotionalPlan);
        return promotionMapper.toDto(promotion);
    }

    @Transactional
    public PromotionDto promoteEvent(long eventId, PromotionalPlan promotionalPlan) {
        promotionValidator.validateEventAlreadyHasPromotion(eventId);
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new BigDecimal(promotionalPlan.getCost()),
            Currency.USD
        );
        paymentValidator.validatePaymentSuccess(paymentResponse);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Event with ID: %d does not exist.", eventId)));
        Promotion promotion = createPromotionForEvent(event, promotionalPlan);
        return promotionMapper.toDto(promotion);
    }

    @Transactional
    public List<UserDto> showPromotedUsersFirst() {
        List<User> regularUsers = userRepository.findAll();
        List<User> promotedUser = userRepository.findPromotedUsers();
        updatePromotionImpressions(promotedUser);
        List<User> combinedUsers = combineUsers(promotedUser, regularUsers);
        return userMapper.usersToUserDTOs(combinedUsers);
    }

    private Promotion createPromotionForUser(User user, PromotionalPlan promotionalPlan) {
        LocalDateTime startDate = LocalDateTime.now();
        Promotion promotion = Promotion.builder()
            .user(user)
            .promotionalPlan(promotionalPlan)
            .impressions(promotionalPlan.getImpressions())
            .startDate(startDate)
            .endDate(startDate.plusDays(promotionalPlan.getDurationInDays()))
            .build();
        userRepository.save(user);
        promotionRepository.save(promotion);
        return promotion;
    }

    private Promotion createPromotionForEvent(Event event, PromotionalPlan promotionalPlan) {
        LocalDateTime startDate = LocalDateTime.now();
        Promotion promotion = Promotion.builder()
            .event(event)
            .promotionalPlan(promotionalPlan)
            .impressions(promotionalPlan.getImpressions())
            .startDate(startDate)
            .endDate(startDate.plusDays(promotionalPlan.getDurationInDays()))
            .build();
        eventRepository.save(event);
        promotionRepository.save(promotion);
        return promotion;
    }

    private void updatePromotionImpressions(List<User> promotedUsers) {
        promotedUsers.forEach(user -> {
                Promotion promotion = user.getPromotion();
                promotion.setImpressions(promotion.getImpressions() - 1);
                promotionRepository.save(promotion);
            }
        );
    }

    private List<User> combineUsers(List<User> promotedUsers, List<User> regularUsers) {
        List<User> combinedUsers = new ArrayList<>(promotedUsers);
        combinedUsers.retainAll(regularUsers);
        return combinedUsers;
    }
}
