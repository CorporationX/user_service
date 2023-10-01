package school.faang.user_service.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ScheduledEventCleanupThreadPoolConfig {
    @Value("${event.cleanup.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${event.cleanup.thread-pool.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${event.cleanup.thread-pool.keep-alive-time}")
    private int keepAliveTime;

    @Value("${event.cleanup.thread-pool.time-unit}")
    private String timeUnit;

    @Bean("scheduledEventCleanupThreadPoolExecutor")
    public ThreadPoolExecutor scheduledEventCleanupThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.valueOf(timeUnit),
                new LinkedBlockingQueue<>()
        );
    }
}
