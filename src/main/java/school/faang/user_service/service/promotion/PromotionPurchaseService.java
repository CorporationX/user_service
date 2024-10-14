package school.faang.user_service.service.promotion;

import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.promotion.PromotionValidator;

@Slf4j
@Service
@AllArgsConstructor
public class PromotionPurchaseService {

    private final PromotionMapper promotionMapper;
    private final PromotionRepository promotionRepository;
    private final PromotionValidator promotionValidator;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public PromotionDto buyPromotion(Long userId, PromotionType promotionType, PromotionTarget promotionTarget) {
        log.info("Attempting to purchase promotion for userId: {} with type: {} and target: {}", userId, promotionType, promotionTarget);

        promotionValidator.validatePromotionAlreadyExistsByUserId(userId);

        User user = userService.getUserById(userId);
        log.debug("User retrieved: {}", user);

        Promotion promotion = createPromotion(user, promotionType, promotionTarget);
        log.debug("Promotion created: {}", promotion);

        Long paymentNumber = promotionRepository.getPromotionPaymentNumber();
        log.debug("Generated payment number: {}", paymentNumber);

        promotionRepository.save(promotion);
        log.debug("Promotion saved for userId: {} with paymentNumber: {}", userId, paymentNumber);

        paymentService.sendPayment(promotionType.getPrice(), paymentNumber);
        log.info("Payment sent for userId: {} with amount: {}", userId, promotionType.getPrice());

        return promotionMapper.toPromotionDto(promotion);
    }

    private Promotion createPromotion(User user, PromotionType promotionType, PromotionTarget promotionTarget) {
        return Promotion.builder()
                .user(user)
                .showCount(promotionType.getShowCount())
                .priority(promotionType.getPriority())
                .target(promotionTarget.toString())
                .build();
    }
}
