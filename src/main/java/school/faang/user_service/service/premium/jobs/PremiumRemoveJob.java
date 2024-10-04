package school.faang.user_service.service.premium.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumRemover;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemoveJob implements Job {
    private final PremiumRemover premiumRemover;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        premiumRemover.removePremium();
    }
}
