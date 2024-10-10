package school.faang.user_service.config.executor;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class TaskExecutorConfig {

    @Value("${scheduler.thread-count}")
    private int threadCounts;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCounts);
        executor.setMaxPoolSize(threadCounts);
        executor.setQueueCapacity(threadCounts * 1000);
        return executor;
    }
}
