package school.faang.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService noticePool(){
        return new ThreadPoolExecutor(10, 15, 5000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10000));
    }
}
