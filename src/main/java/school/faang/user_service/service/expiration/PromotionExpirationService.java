package school.faang.user_service.service.expiration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promotiom.PromotionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionExpirationService implements ExpirationService {
    private final PromotionRepository promotionRepository;

    @Override
    public void checkExpirations(LocalDateTime currentDate) {
        List<Promotion> promotions = promotionRepository.findAllByEndDate(currentDate);
        promotionRepository.deleteAll(promotions);
    }
}
