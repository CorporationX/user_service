package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.model.enums.Currency;
import school.faang.user_service.model.dto.PaymentRequest;
import school.faang.user_service.model.dto.PaymentResponse;
import school.faang.user_service.model.enums.PaymentStatus;
import school.faang.user_service.model.dto.PromotionDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.model.enums.PromotionType;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.PromotionService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionMapper promotionMapper;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionDto buyPromotion(long userId, PromotionType type, String target) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        promotionRepository.findByUserId(userId).ifPresent(promotion -> {
            if (promotion.getPromotionTarget().equals(target)) {
                throw new ExistingPurchaseException("User already has an active promotion subscription" +
                        " with the same target");
            }
        });

        payPromotion(type, user, target);

        Promotion savedPromotion = savePromotion(user, type, target);
        return promotionMapper.toPromotionDto(savedPromotion);
    }

    private void payPromotion(PromotionType type, User user, String target) {
        long paymentNumber = promotionRepository.getPromotionPaymentNumber();
        PaymentRequest paymentRequest =
                new PaymentRequest(paymentNumber, new BigDecimal(type.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse response = paymentResponseEntity.getBody();

        if (response == null) {
            log.error("No response received from payment service for user {}.", user.getUsername());
            throw new PaymentFailureException("No response from payment service.");
        }

        if (response.status() == PaymentStatus.FAIL) {
            log.error("Payment for {} promotion failed for user {}.", target, user.getUsername());
            throw new PaymentFailureException(String.format("Failure to effect %s promotion payment for user %s.",
                    target, user.getUsername()));
        }

        log.info("Payment response received for user {}: {}", user.getUsername(), response.message());
    }

    private Promotion savePromotion(User user, PromotionType type, String target) {
        Promotion promotion = new Promotion();
        promotion.setUser(user);
        promotion.setPriorityLevel(type.getPriorityLevel());
        promotion.setRemainingShows(type.getNumberOfShows());
        promotion.setPromotionTarget(target);
        return promotionRepository.save(promotion);
    }
}
