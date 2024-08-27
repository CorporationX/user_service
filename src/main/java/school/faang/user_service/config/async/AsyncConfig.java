package school.faang.user_service.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Value("${spring.task.execution.pool.core-size}")
    private int coreSize;
    @Value("${spring.task.execution.pool.max-size}")
    private int maxSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution.pool.keep-alive}")
    private int keepAlive;
    @Value("${spring.task.execution.pool.thread-name-prefix}")
    private String threadName;
    @Value("${spring.task.execution.pool.wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;
    @Value("${spring.task.execution.pool.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Bean(name = "asyncExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(threadName);

        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);

        return executor;
    }
}
