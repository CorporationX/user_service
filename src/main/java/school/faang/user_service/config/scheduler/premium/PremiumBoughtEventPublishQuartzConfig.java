package school.faang.user_service.config.scheduler.premium;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.service.premium.jobs.PremiumBoughtEventPublishJob;

@Configuration
public class PremiumBoughtEventPublishQuartzConfig {
    private static final String NAME = "premiumBoughtEventPublishJob";
    private static final String GROUP = "premium";

    @Value("${app.quartz-config.premium-bought-event-publish.trigger_interval_sec}")
    private int triggerInterval;

    @Bean
    public JobDetail premiumBoughtEventPublishJobDetail() {
        return JobBuilder.newJob(PremiumBoughtEventPublishJob.class)
                .withIdentity(NAME, GROUP)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger premiumBoughtEventPublishJobTrigger() {
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(triggerInterval)
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(premiumBoughtEventPublishJobDetail())
                .withIdentity(NAME, GROUP)
                .withSchedule(schedule)
                .build();
    }
}
