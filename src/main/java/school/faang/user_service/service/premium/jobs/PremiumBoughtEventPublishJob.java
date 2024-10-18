package school.faang.user_service.service.premium.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.AspectRedisPremiumBoughtEventPublisher;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
@Component
public class PremiumBoughtEventPublishJob implements Job {
    private final AspectRedisPremiumBoughtEventPublisher redisPremiumBoughtEventPublisher;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!redisPremiumBoughtEventPublisher.analyticEventsIsEmpty()) {
            log.info("Publish all premium bought event job execute");
            redisPremiumBoughtEventPublisher.publishAllEvents();
        }
    }
}
