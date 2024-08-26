package school.faang.user_service.config.quartz;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.exception.job.SchedulerConfigurationException;
import school.faang.user_service.job.EventStartEventPublisherJob;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventStartEventPublisherQuartzConfig {

    private final Scheduler scheduler;
    @Value("${scheduler.event-start.interval-second:60}")
    private int intervalInSeconds;

    @PostConstruct
    public void start() {
        try {
            JobDetail jobDetail = createJobDetail();
            Trigger trigger = createTrigger();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Scheduled job '{}' with trigger '{}'", jobDetail.getKey().getName(), trigger.getKey().getName());
        } catch (SchedulerException e) {
            log.error("Scheduler failed to schedule jobs: {}", e.getMessage(), e);
            throw new SchedulerConfigurationException("Failed to schedule jobs", e);
        }
    }

    private JobDetail createJobDetail() {
        return JobBuilder
                .newJob(EventStartEventPublisherJob.class)
                .withIdentity("eventStartEventPublisherJob")
                .build();
    }

    private Trigger createTrigger() {
        return TriggerBuilder
                .newTrigger()
                .withIdentity("eventStartEventPublisherTrigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();
    }
}
