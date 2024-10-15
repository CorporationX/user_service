package school.faang.user_service.service.user.view.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.view.publisher.AOPRedisProfileViewEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class ProfileViewPublishJob implements Job {
    private final AOPRedisProfileViewEventPublisher redisProfileViewEventPublisher;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!redisProfileViewEventPublisher.analyticEventsIsEmpty()) {
            log.info("Publish all profile view dtos execute");
            redisProfileViewEventPublisher.publishAllEvents();
        }
    }
}
