package school.faang.user_service.service.premium.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.premium.async.AsyncPremiumService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemoveJob implements Job {
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    private final PremiumService premiumService;
    private final AsyncPremiumService asyncPremiumService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Remove all expired premiums job execute");
        List<Premium> premiums = premiumService.findAllByEndDateBefore(LocalDateTime.now());
        List<List<Premium>> batches = ListUtils.partition(premiums, batchSize);

        batches.forEach(asyncPremiumService::deleteAllPremiumsByIdAsync);
    }
}
