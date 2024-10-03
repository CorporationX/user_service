package school.faang.user_service.service.premium;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TestAsyncConfig {
    @Bean
    public Executor asyncExecutor() {
        return new SyncTaskExecutor();
    }
}
