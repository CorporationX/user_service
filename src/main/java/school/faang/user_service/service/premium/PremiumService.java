package school.faang.user_service.service.premium;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;

    @Value("${premium.remover.poolSize}")
    private int poolSize;

    private ExecutorService executor;


    @PostConstruct
    public void init() {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }


    public void removeExpiredPremiums(int batchSize) {
        List<Premium> premiumList = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        List<List<Premium>> batches = createBatches(premiumList, batchSize);
        List<Future<?>> futures = new ArrayList<>();
        for (List<Premium> batch : batches) {
            futures.add(executor.submit(() -> premiumRepository.deleteAll(batch)));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private List<List<Premium>> createBatches(List<Premium> list, int batchSize) {
        List<List<Premium>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }
}
