package school.faang.user_service.config.scheduler.user;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.service.user.view.job.ProfileViewPublishJob;

@Configuration
public class ProfileViewPublishQuartzConfig {
    private static final String NAME = "profileViewPublishJob";
    private static final String GROUP = "user";

    @Value("${app.quartz-config.profile-view-publish.trigger_interval_sec}")
    private int triggerInterval;

    @Bean
    public JobDetail profileViewPublishJobDetail() {
        return JobBuilder.newJob(ProfileViewPublishJob.class)
                .withIdentity(NAME, GROUP)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger profileViewPublishJobTrigger() {
        SimpleScheduleBuilder croneSchedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(triggerInterval)
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(profileViewPublishJobDetail())
                .withIdentity(NAME, GROUP)
                .withSchedule(croneSchedule)
                .build();
    }
}
