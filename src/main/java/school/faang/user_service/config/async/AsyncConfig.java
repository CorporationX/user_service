package school.faang.user_service.config.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.exception.handler.CustomAsyncExceptionHandler;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.core-pool-size}")
    private int corePoolSize;
    @Value("${async.max-pool-size}")
    private int maxPoolSize;
    @Value("${async.queue-capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AsyncTask-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfigTt {
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
