package school.faang.user_service.config.scheduler.premium;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.service.premium.jobs.PremiumRemoveJob;

@Configuration
public class PremiumRemoveQuartzConfig {

    @Bean
    public JobDetail premiumRemoveJobDetail() {
        return JobBuilder.newJob(PremiumRemoveJob.class)
                .withIdentity("premiumRemoveJobDetail", "premium")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger premiumRemoveTrigger() {
//        CronScheduleBuilder croneScheduler = CronScheduleBuilder.cronSchedule("*/10 * * * * *");
        SimpleScheduleBuilder simpleScheduler = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(premiumRemoveJobDetail())
                .withIdentity("premiumRemoveJobTrigger", "premium")
                .withSchedule(simpleScheduler)
                .build();
    }
}
