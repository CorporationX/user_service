package school.faang.user_service.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolDistributor {

    @Bean
    public ThreadPoolTaskExecutor customThreadPool() {
        return new ThreadPoolTaskExecutor();
    }
}