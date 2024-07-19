package school.faang.user_service.service.expiration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.util.ListPartitioner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class PremiumExpirationService implements ExpirationService {
    private final PremiumRepository premiumRepository;
    private final ListPartitioner listPartitioner;
    private final Executor premiumExpirationExecutor;

    @Value("${executor.premium-expiration.corePoolSize}")
    private int corePoolSize;

    @Override
    @Async("premiumExpirationExecutor")
    public void processExpiredItems(LocalDateTime currentDate) {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(currentDate);
        List<List<Premium>> partitions = listPartitioner.partitionList(expiredPremiums, corePoolSize);
        partitions.forEach(partition -> premiumExpirationExecutor.execute(() -> premiumRepository.deleteAll(partition)));
    }
}
