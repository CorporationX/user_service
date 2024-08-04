package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.model.EntityType;
import school.faang.user_service.client.paymentService.model.PaymentResponse;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.entity.promotion.PremiumPaymentRequest;
import school.faang.user_service.entity.promotion.PromotionPaymentRequest;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PremiumPaymentRequestRepository;
import school.faang.user_service.repository.promotion.PromotionPaymentRequestRepository;

import java.util.UUID;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PremiumPaymentRequestRepository premiumPaymentRequestRepository;
    private final PromotionPaymentRequestRepository promotionPaymentRequestRepository;
    private final PromotionService promotionService;
    private final PremiumService premiumService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void handlePaymentResponse(PaymentResponse response) {
        switch (response.product()) {
            case PREMIUM -> handlePremium(response);
            case PROMOTION -> handlePromotion(response);
        }
        log.info("Success handle payment request: {}, status: {}",
                response.requestId(), response.status());
    }

    private void handlePremium(PaymentResponse response) {
        PremiumPaymentRequest request = findPremiumPaymentRequest(response);
        if (response.status().equals(PaymentStatus.SUCCESS)) {
            premiumService.savePremium(request.getUserId(), request.getDays());
        }
        premiumPaymentRequestRepository.delete(request);
    }

    private void handlePromotion(PaymentResponse response) {
        PromotionPaymentRequest request = findPromotionPaymentRequest(response);
        if (response.status().equals(PaymentStatus.SUCCESS)) {
            promotionService.savePromotion(EntityType.valueOf(request.getEntityType()),
                    request.getEntityId(), request.getTariffId().getId());
        }
        promotionPaymentRequestRepository.delete(request);
    }


    private PremiumPaymentRequest findPremiumPaymentRequest(PaymentResponse paymentResponse) {
        return premiumPaymentRequestRepository
                .findById(UUID.fromString(paymentResponse.requestId()))
                .orElseThrow(() -> new PaymentException(
                        "Not found payment request with id: " + paymentResponse.requestId()));
    }

    private PromotionPaymentRequest findPromotionPaymentRequest(PaymentResponse paymentResponse) {
        return promotionPaymentRequestRepository
                .findById(UUID.fromString(paymentResponse.requestId()))
                .orElseThrow(() -> new PaymentException(
                        "Not found payment request with id: " + paymentResponse.requestId()));
    }
}
