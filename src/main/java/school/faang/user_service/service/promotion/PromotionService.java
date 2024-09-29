package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.validator.promotion.PromotionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class PromotionService {

    private final PaymentServiceClient paymentServiceClient;
    private final PromotionMapper promotionMapper;
    private final PromotionRepository promotionRepository;
    private final PromotionValidator promotionValidator;
    private final UserValidator userValidator;

    @Transactional
    public PromotionDto buyPromotion(Long userId, PromotionType promotionType, PromotionTarget promotionTarget) {
        userValidator.validateUserIsExisted(userId);
        promotionValidator.validatePromotionAlreadyExistsByUserId(userId);

        PromotionDto promotionDto = createPromotion(userId, promotionType, promotionTarget);
        sendPayment(promotionType, userId);
        promotionRepository.save(promotionMapper.toEntity(promotionDto));

        return promotionDto;
    }

    @Transactional
    public void markAsShowPromotions(List<Long> promotionIds) {
        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseShowCount(promotionIds);
        }
    }

    @Transactional
    public void removeExpiredPromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions();
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }

    private void sendPayment(PromotionType price, Long userId) {
        long paymentNumber = promotionRepository.getPromotionPaymentNumber();
        PaymentRequest request = new PaymentRequest(
                paymentNumber,
                BigDecimal.valueOf(price.getPrice()),
                Currency.USD
        );

        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(request);
        PaymentResponse paymentResponse = response.getBody();

        if (paymentResponse == null) {
            throw new PaymentFailureException("No response from payment service.");
        }

        if (paymentResponse.status() == PaymentStatus.FAILURE) {
            throw new PaymentFailureException("Failure to effect promotion payment for userId " + userId);
        }
    }

    private PromotionDto createPromotion(Long userId, PromotionType promotionType, PromotionTarget promotionTarget) {
        PromotionDto promotion = new PromotionDto();
        promotion.setUserId(userId);
        promotion.setShowCount(promotionType.getShowCount());
        promotion.setPriority(promotionType.getPriority());
        promotion.setTarget(promotionTarget);

        return promotion;
    }
}
