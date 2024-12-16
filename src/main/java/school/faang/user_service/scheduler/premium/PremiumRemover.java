package school.faang.user_service.scheduler.premium;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumRemover {
    private final PremiumRepository premiumRepository;
    private final PremiumService premiumService;
    private final ListPartitioner listPartitioner;

    @Value("${application.premium.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${application.premium.time-to-run-remove-expired-premium}")
    public void removeExpiredPremium(){
        List<Premium> premiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        List<List<Premium>> batches = listPartitioner.partition(premiums, batchSize);

        if (premiums.isEmpty()){
            log.info("There is no expired premium subscription found");
            throw new EntityNotFoundException("There is no expired premium subscription found");
        }
        log.info("Start to remove all expired premium accesses");

        for (List<Premium> batch : batches){
            premiumService.removeExpiredPremium(batch);
        }

        log.info("All expired premium accesses were successfully removed");
    }
}
