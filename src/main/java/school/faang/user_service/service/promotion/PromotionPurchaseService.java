package school.faang.user_service.service.promotion;

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
        promotionValidator.validatePromotionAlreadyExistsByUserId(userId);

        User user = userService.getUserById(userId);
        Promotion promotion = createPromotion(user, promotionType, promotionTarget);
        Long paymentNumber = promotionRepository.getPromotionPaymentNumber();

        paymentService.sendPayment(promotionType.getPrice(), paymentNumber);

        promotionRepository.save(promotion);

        return promotionMapper.toDto(promotion);
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
