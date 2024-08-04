package school.faang.user_service.service.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public ExecutorService getThreadPool() {
        return new ThreadPoolExecutor(quantityPollSize, maxQuantityPollSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(maxNumberQueuesForThread));
    }
}
