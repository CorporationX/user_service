package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PromotionManagementService {

    private final PromotionRepository promotionRepository;

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
}
