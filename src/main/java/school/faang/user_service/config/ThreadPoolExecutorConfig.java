package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolExecutorConfig {

    @Value("${thread-pool.size.min}")
    private int POOL_MIN_SIZE;

    @Value("${thread-pool.size.max}")
    private int POOL_MAX_SIZE;

    @Value("${thread-pool.keep-alive.time}")
    private long KEEP_ALIVE_TIME;

    @Value("${thread-pool.keep-alive.time-unit}")
    private TimeUnit KEEP_ALIVE_TIME_UNIT;

    @Value("${thread-pool.queue-size}")
    private int POOL_QUEUE_SIZE;


    @Bean
    public ThreadPoolExecutor threadPool() {
        return new ThreadPoolExecutor(POOL_MIN_SIZE,
                POOL_MAX_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>(POOL_QUEUE_SIZE));
    }
}
