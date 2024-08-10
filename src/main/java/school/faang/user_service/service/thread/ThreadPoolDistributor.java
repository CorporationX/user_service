package school.faang.user_service.service.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolDistributor {
    @Value("${thread.pool.coreSize}")
    private int quantityPollSize;
    @Value("${thread.pool.maxSize}")
    private int maxQuantityPollSize;
    @Value("${thread.pool.keepAliveTimeMillisecond}")
    private long keepAliveTime;
    @Value("${thread.pool.maxNumberQueues}")
    private int maxNumberQueuesForThread;

    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(quantityPollSize);
        executor.setMaxPoolSize(maxQuantityPollSize);
        executor.setQueueCapacity(maxQuantityPollSize);
        executor.initialize();
        return executor;

    }
}
