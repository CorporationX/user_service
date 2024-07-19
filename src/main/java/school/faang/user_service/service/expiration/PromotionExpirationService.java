package school.faang.user_service.service.expiration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promotiom.PromotionRepository;
import school.faang.user_service.util.ListPartitioner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class PromotionExpirationService implements ExpirationService {
    private final PromotionRepository promotionRepository;
    private final ListPartitioner listPartitioner;
    private final Executor promotionExpirationExecutor;

    @Value("${executor.promotion-expiration.corePoolSize}")
    private int corePoolSize;

    @Override
    public void processExpiredItems(LocalDateTime currentDate) {
        List<Promotion> expiredPromotions = promotionRepository.findAllByEndDate(currentDate);
        List<List<Promotion>> partitions = listPartitioner.partitionList(expiredPromotions, corePoolSize);
        partitions.forEach(partition -> promotionExpirationExecutor.execute(() -> promotionRepository.deleteAll(partition)));
    }
}
