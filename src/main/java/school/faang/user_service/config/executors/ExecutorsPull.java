package school.faang.user_service.config.executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.executors.PullUserService;

@Configuration
public class ExecutorsPull {
    @Bean
    public PullUserService pullUserService() {
        return new PullUserService();
    }
}
