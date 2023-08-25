package school.faang.user_service.config.thread_pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Value("${event.publisher.scheduler.thread-pool.core-pool-size}")
    private int scheduledThreadPoolCorePoolSize;

    @Bean("scheduledThreadPoolExecutor")
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor(){
        return new ScheduledThreadPoolExecutor(scheduledThreadPoolCorePoolSize);

    }
}
