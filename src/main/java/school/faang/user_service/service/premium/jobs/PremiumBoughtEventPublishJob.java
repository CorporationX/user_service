package school.faang.user_service.service.premium.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumBoughtEventService;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class PremiumBoughtEventPublishJob implements Job {
    private final PremiumBoughtEventService premiumBoughtEventService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!premiumBoughtEventService.premiumBoughtEventDtosIsEmpty()) {
            log.info("Publish all premium bought event job execute");
            premiumBoughtEventService.publishAllPremiumBoughtEvents();
        }
    }
}
